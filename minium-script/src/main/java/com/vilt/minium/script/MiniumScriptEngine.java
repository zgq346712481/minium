package com.vilt.minium.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

public class MiniumScriptEngine {

	private static final String RHINO_BOOTSTRAP_JS = "rhino/bootstrap.js";
	private static final String BOOTSTRAP_EXTS_JS = "rhino/bootstrap-extension.js";
	
	private static final Logger logger = LoggerFactory.getLogger(MiniumScriptEngine.class);
	private ClassLoader classLoader;
	
	private ScriptEngine engine;
	private String scriptsDir;
	private WebElementDrivers webElementsDrivers;
	private Map<String, Object> context = Maps.newHashMap();
	
	public MiniumScriptEngine() {
		this(MiniumScriptEngine.class.getClassLoader());
	}
	
	public MiniumScriptEngine(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public void setWebElementsDrivers(WebElementDrivers webElementsDrivers) {
		this.webElementsDrivers = webElementsDrivers;
	}
	
	public void scriptsDir(String scriptsDir) {
		this.scriptsDir = scriptsDir;
	}
	
	public void put(String varName, Object object) {
		if (engine != null) {
			engine.put(varName, object);
		}
		else {
			context.put(varName, object);
		}
	}
	
	public Object eval(String expression) throws Exception {
		logger.debug("Evaluating expression: {}", expression);
		
		try {
			maybeInitScope();
			
			Object result = engine.eval(expression);
			return result;
		}
		catch(Exception e) {
			logger.error("Evaluation of {} failed", expression, e);
			throw e;
		}
	}

	public Object load(String path) {
		logger.debug("Evaluating script: {}", path);

		FileReader reader = null;
		try {
			maybeInitScope();
		
			File file = new File(path);
			if (!file.isAbsolute()) {
				file = new File(scriptsDir, path);
			}
			
			reader = new FileReader(file);
			return engine.eval(reader);
		} catch (Exception e) {
			logger.error("Evaluation of script {} failed", path, e);
			throw new RuntimeException(e);
		} finally {
			try { Closeables.close(reader, true); } catch (IOException e) { }
		}
	}
	
	protected void maybeInitScope() throws IOException {
		if (engine == null) {
			try {
				engine = new ScriptEngineManager().getEngineByName("js");
				engine.put("webElementsDrivers", webElementsDrivers);
				engine.put("scriptEngine", this);
	
				logger.debug("Loading minium bootstrap file");
				engine.eval(resourceFileReader(RHINO_BOOTSTRAP_JS));
				
				Enumeration<URL> resources = classLoader.getResources(BOOTSTRAP_EXTS_JS);
				
				while(resources.hasMoreElements()) {
					URL resourceUrl = resources.nextElement();
					Reader reader = resourceUrlReader(resourceUrl);
					if (reader != null) {
						logger.debug("Loading extension bootstrap from '{}'", resourceUrl.toString());
						engine.eval(reader);
					}
				}
				
				for (String varName : context.keySet()) {
					engine.put(varName, context.get(varName));
				}
			} catch (ScriptException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private Reader resourceFileReader(String resourceName) {
		InputStream is = classLoader.getResourceAsStream(resourceName);
		if (is == null) return null;
		return new BufferedReader(new InputStreamReader(is));
	}

	private Reader resourceUrlReader(URL resourceUrl) {
		try {
			InputStream is = resourceUrl.openStream();
			return new BufferedReader(new InputStreamReader(is));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

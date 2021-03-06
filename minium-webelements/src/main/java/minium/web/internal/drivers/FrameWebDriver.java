/*
 * Copyright (C) 2015 The Minium Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package minium.web.internal.drivers;

import static com.google.common.base.MoreObjects.toStringHelper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class FrameWebDriver extends BaseDocumentWebDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(FrameWebDriver.class);

    private final DocumentWebElement frameElem;

    public FrameWebDriver(DocumentWebElement frameElem) {
        super(getNativeWebDriver(frameElem));
        this.frameElem = frameElem;
    }

    @Override
    public void ensureSwitch() {
        InternalDocumentWebDriver parentDocumentDriver = parentDocumentDriver();
        Preconditions.checkState(Objects.equal(parentDocumentDriver, frameElem.getWrappedDriver()), "Frame %s does not belongs to %s", frameElem, parentDocumentDriver);
        parentDocumentDriver.ensureSwitch();
        WebElement nativeFrameElem = frameElem.getWrappedWebElement();
        webDriver.switchTo().frame(nativeFrameElem);
        LOGGER.trace("Switched to frame {}", frameElem);
    }

    @Override
    public String toString() {
        return toStringHelper(FrameWebDriver.class.getSimpleName())
                .addValue(frameElem)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), frameElem);
    }

    @Override
    public boolean isClosed() {
        return parentDocumentDriver().isClosed();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == FrameWebDriver.class && equalFields((FrameWebDriver) obj);
    }

    protected boolean equalFields(FrameWebDriver obj) {
        return super.equalFields(obj) && Objects.equal(obj.frameElem, frameElem);
    }

    protected InternalDocumentWebDriver parentDocumentDriver() {
        return (InternalDocumentWebDriver) frameElem.getWrappedDriver();
    }

    private static WebDriver getNativeWebDriver(DocumentWebElement frameElem) {
        return ((InternalDocumentWebDriver) frameElem.getWrappedDriver()).nativeWebDriver();
    }
}
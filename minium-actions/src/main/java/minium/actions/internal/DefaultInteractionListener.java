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
package minium.actions.internal;

import minium.actions.InteractionEvent;
import minium.actions.InteractionListener;

/**
 * The listener interface for receiving defaultInteraction events.
 * The class that is interested in processing a defaultInteraction
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addDefaultInteractionListener<code> method. When
 * the defaultInteraction event occurs, that object's appropriate
 * method is invoked.
 *
 * @see DefaultInteractionEvent
 */
public abstract class DefaultInteractionListener implements InteractionListener {

    /* (non-Javadoc)
     * @see com.vilt.minium.actions.InteractionListener#onEvent(com.vilt.minium.actions.InteractionEvent)
     */
    @Override
    public void onEvent(InteractionEvent event) {
        switch (event.getType()) {
        case BEFORE_WAIT:
            onBeforeWaitEvent((BeforeWaitInteractionEvent) event);
            break;
        case BEFORE:
            onBeforeEvent((BeforeInteractionEvent) event);
            break;
        case AFTER_SUCCESS:
            onAfterSuccessEvent((AfterSuccessInteractionEvent) event);
            onAfterEvent((AfterInteractionEvent) event);
            break;
        case AFTER_FAIL:
            onAfterFailEvent((AfterFailInteractionEvent) event);
            onAfterEvent((AfterInteractionEvent) event);
            break;
        }
    }

    /**
     * On before wait event.
     *
     * @param event the event
     */
    protected void onBeforeWaitEvent(BeforeWaitInteractionEvent event) {
        // do nothing
    }

    /**
     * On before event.
     *
     * @param event the event
     */
    protected void onBeforeEvent(BeforeInteractionEvent event) {
        // do nothing
    }

    /**
     * On after event.
     *
     * @param event the event
     */
    protected void onAfterEvent(AfterInteractionEvent event) {
        // do nothing
    }

    /**
     * On after interacting event.
     *
     * @param event the event
     */
    protected void onAfterSuccessEvent(AfterSuccessInteractionEvent event) {
        // do nothing
    }

    /**
     * On after failing event.
     *
     * @param event the event
     */
    protected void onAfterFailEvent(AfterFailInteractionEvent event) {
        // do nothing
    }
}

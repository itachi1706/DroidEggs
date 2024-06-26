/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itachi1706.droideggs.forward_ported_code;

import android.transition.Transition;

/**
 * This adapter class provides empty implementations of the methods from {@link
 * android.transition.Transition.TransitionListener}.
 * Any custom listener that cares only about a subset of the methods of this listener can
 * simply subclass this adapter class instead of implementing the interface directly.
 */
public abstract class TransitionListenerAdapter implements Transition.TransitionListener {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTransitionStart(Transition transition) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTransitionEnd(Transition transition) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTransitionCancel(Transition transition) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTransitionPause(Transition transition) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onTransitionResume(Transition transition) {
    }
}


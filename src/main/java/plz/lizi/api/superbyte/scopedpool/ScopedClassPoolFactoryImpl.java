/*
 * Javassist, a Java-bytecode translator toolkit.
 * Copyright (C) 1999- Shigeru Chiba. All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License.  Alternatively, the contents of this file may be used under
 * the terms of the GNU Lesser General Public License Version 2.1 or later,
 * or the Apache License Version 2.0.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 */

package plz.lizi.api.superbyte.scopedpool;

import plz.lizi.api.superbyte.ClassPool;

/**
 * An implementation of factory.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @version $Revision: 1.5 $
 */
public class ScopedClassPoolFactoryImpl implements ScopedClassPoolFactory {
    /**
     * Makes an instance.
     */
    public ScopedClassPool create(ClassLoader cl, ClassPool src,
                                  ScopedClassPoolRepository repository) {
        return new ScopedClassPool(cl, src, repository, false);
    }

    /**
     * Makes an instance.
     */
    public ScopedClassPool create(ClassPool src,
                                  ScopedClassPoolRepository repository) {
        return new ScopedClassPool(null, src, repository, true);
    }
}

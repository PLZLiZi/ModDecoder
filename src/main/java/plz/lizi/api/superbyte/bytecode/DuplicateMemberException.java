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

package plz.lizi.api.superbyte.bytecode;

import plz.lizi.api.superbyte.CannotCompileException;

/**
 * An exception thrown when adding a duplicate member is requested.
 *
 * @see ClassFile#addMethod(MethodInfo)
 * @see ClassFile#addField(FieldInfo)
 */
public class DuplicateMemberException extends CannotCompileException {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;

    public DuplicateMemberException(String msg) {
        super(msg);
    }
}

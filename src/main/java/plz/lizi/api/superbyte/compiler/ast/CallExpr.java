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

package plz.lizi.api.superbyte.compiler.ast;

import plz.lizi.api.superbyte.compiler.CompileError;
import plz.lizi.api.superbyte.compiler.MemberResolver;
import plz.lizi.api.superbyte.compiler.TokenId;

/**
 * Method call expression.
 */
public class CallExpr extends Expr {
    /** default serialVersionUID */
    private static final long serialVersionUID = 1L;
    private MemberResolver.Method method;  // cached result of lookupMethod()

    private CallExpr(ASTree _head, ASTList _tail, int lineNumber) {
        super(TokenId.CALL, _head, _tail, lineNumber);
        method = null;
    }

    public void setMethod(MemberResolver.Method m) {
        method = m;
    }

    public MemberResolver.Method getMethod() {
        return method;
    }

    public static CallExpr makeCall(ASTree target, ASTree args, int lineNumber) {
        return new CallExpr(target, new ASTList(args, lineNumber), lineNumber);
    }

    @Override
    public void accept(Visitor v) throws CompileError { v.atCallExpr(this); }
}

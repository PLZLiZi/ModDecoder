// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.java.decompiler.main.decompiler;

import java.io.PrintStream;

public class EmptyLogger extends PrintStreamLogger {

	public EmptyLogger(PrintStream printStream) {
		super(printStream);
	}

	@Override
	public void writeMessage(String message, Severity severity) {}

	@Override
	public void writeMessage(String message, Severity severity, Throwable t) {}

	@Override
	public void startReadingClass(String className) {}

	@Override
	public void endReadingClass() {}

	@Override
	public void startClass(String className) {}

	@Override
	public void endClass() {}

	@Override
	public void startMethod(String methodName) {}

	@Override
	public void endMethod() {}

	@Override
	public void startWriteClass(String className) {}

	@Override
	public void endWriteClass() {}
}

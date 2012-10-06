/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @author Serguei S.Zapreyev
 *
 */

/**
 * ###############################################################################
 * ###############################################################################
 *TODO LIST:
 *1.Provide correct processing the case if process isn't started because of some  *reason *2.Clean and develop the

 native support
 * 3. Think of the default/undefault buffering
 * 3. Runtime.SubProcess.SubInputStream.read(b, off, len) and
 *    Runtime.SubProcess.SubErrorStream.read(b, off, len) should be effectively
 *    reimplemented on the native side.
 * ###############################################################################
 * ###############################################################################
 */

package java.lang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.harmony.kernel.vm.VM;
import org.apache.harmony.lang.RuntimePermissionCollection;
import org.apache.harmony.luni.internal.net.www.protocol.jar.JarURLConnectionImpl;
import org.apache.harmony.luni.util.DeleteOnExit;
import org.apache.harmony.vm.VMStack;

/**
 * Allows Java applications to interface with the environment in which they are
 * running. Applications can not create an instance of this class, but they can
 * get a singleton instance by invoking {@link #getRuntime()}.
 *
 * @see System
 */
public class Runtime
{

	//--------------------------------------------------------------------------------
	//  Nested protected Runtime.SubProcess class:
	//--------------------------------------------------------------------------------

	static final class SubProcess extends Process
	{


		final static class SubInputStream extends InputStream
		{

			long streamHandle;

			/**
			 * Constructs a new SubInputStream instance.
			 */
			SubInputStream()
			{
				this.streamHandle = -1;
			}

			/**
			 * Reads the next byte of data from the input stream....
			 *
			 * @see int read() from InputStream
			 */
			private final native int readInputByte0(long handle) throws IOException;

			public final int read() throws IOException
			{
				return readInputByte0(this.streamHandle);
			}

			/**
			 * Returns the number of bytes that can be read (or skipped over) from
			 * this input stream without blocking by the next caller
			 * of a method for this input stream...
			 *
			 * @see int available() from InputStream
			 */
			private final native int available0(long handle);

			public final int available() throws IOException
			{
				return available0(this.streamHandle);
			}

			/**
			 * Reads len bytes from input stream ...
			 *
			 * @see read(byte[], int, int) from InputStream
			 */
			public int read(byte[] b, int off, int len) throws IOException
			{
				if(b == null)
				{
					throw new NullPointerException();
				}

				if(off < 0 || len < 0 || off + len > b.length)
				{
					throw new IndexOutOfBoundsException();
				}

				if(len == 0)
				{
					return 0;
				}
				int c = read();
				if(c == -1)
				{
					return -1;
				}
				b[off] = (byte) c;

				int i = 1;
				for(; i < len; i++)
				{
					try
					{
						if(available() != 0)
						{
							int r = read();
							if(r != -1)
							{
								b[off + i] = (byte) r;
								continue;
							}
							return i;
						}
					}
					catch(IOException e)
					{
						break; //If any subsequent call to read() results in a IOException
					}
					break; //but a smaller number may be read, possibly zero.
				}
				return i;
			}

			/**
			 * Closes this input stream and releases any system resources associated
			 * with the stream.
			 *
			 * @see close() from InputStream
			 */
			private final native void close0(long handle) throws IOException;

			public final synchronized void close() throws IOException
			{
				if(streamHandle == -1)
					return;
				close0(streamHandle);
				streamHandle = -1;
			}

			protected void finalize() throws Throwable
			{
				close();
			}
		}

		//--------------------------------------------------------------------------------
		//  Nested Class Runtime.SubProcess.SubOutputStream :
		//--------------------------------------------------------------------------------

		/**
		 * Extends OutputStream class.
		 */
		final static class SubOutputStream extends OutputStream
		{

			long streamHandle;

			/**
			 * Constructs a new SubOutputStream instance.
			 */
			SubOutputStream()
			{
				this.streamHandle = -1;
			}

			/**
			 * Writes the specified byte to this output stream ...
			 *
			 * @see write(int) from OutputStream
			 */
			private final native void writeOutputByte0(long handle, int bt);

			public final void write(int b) throws IOException
			{
				writeOutputByte0(this.streamHandle, b);
			}

			/**
			 * Writes len bytes from the specified byte array starting at
			 * offset off to this output stream ...
			 *
			 * @see write(byte[], int, int) from OutputStream
			 */
			private final native void writeOutputBytes0(long handle, byte[] b, int off, int len);

			public final void write(byte[] b, int off, int len) throws IOException
			{
				if(b == null)
				{
					throw new NullPointerException();
				}

				if(off < 0 || len < 0 || off + len > b.length)
				{
					throw new IndexOutOfBoundsException();
				}

				writeOutputBytes0(this.streamHandle, b, off, len);
			}

			/**
			 * Writes b.length bytes from the specified byte array to this output stream...
			 *
			 * @see write(byte[]) from OutputStream
			 */
			public final void write(byte[] b) throws IOException
			{
				write(b, 0, b.length);
			}

			/**
			 * Flushes this output stream and forces any buffered output
			 * bytes to be written out ...
			 *
			 * @see flush() from OutputStream
			 */
			private final native void flush0(long handle);

			public final void flush() throws IOException
			{
				flush0(this.streamHandle);
			}

			/**
			 * Closes this output stream and releases any system resources
			 * associated with this stream ...
			 *
			 * @see close() from OutputStream
			 */
			private final native void close0(long handle);

			public final synchronized void close() throws IOException
			{
				if(streamHandle == -1)
					return;
				close0(streamHandle);
				streamHandle = -1;
			}

			protected void finalize() throws Throwable
			{
				close();
			}
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////////////////////////////     Runtime.SubProcess     BODY     //////////////////////////////////
		////////////////////////////////////////////////////////////////////////////////////////////////////////////

		private int processHandle;
		private int processExitCode;
		private OutputStream os;
		private InputStream is;
		private InputStream es;

		/**
		 * An application cannot create its own instance of this class.
		 */
		protected SubProcess()
		{
			this.processHandle = -1;
			this.processExitCode = 0;
			this.os = null;
			this.is = null;
			this.es = null;
		}

		private final native void close0(int handle);

		protected void finalize() throws Throwable
		{
			if(processHandle != -1)
				close0(this.processHandle);
		}

		/**
		 * @see OutputStream.getOutputStream() from Process
		 */
		public final OutputStream getOutputStream()
		{
			return os;
		}

		/**
		 * @see InputStream.getInputStream() from Process
		 */
		public final InputStream getInputStream()
		{
			return is;
		}

		/**
		 * @see InputStream getErrorStream() from Process
		 */
		public final InputStream getErrorStream()
		{
			return es;
		}

		private final native boolean getState0(int thisProcessHandle);

		private final native void createProcess0(Object[] cmdarray, Object[] envp, String dir, long[] ia);

		protected final void execVM(String[] cmdarray, String[] envp, String dir) throws IOException
		{
			// Do all java heap allocation first, in order to throw OutOfMemory
			// exception early, before we have actually executed the process.
			// Otherwise we should do somewhat complicated cleanup.
			SubProcess.SubOutputStream os1 = new SubProcess.SubOutputStream();
			SubProcess.SubInputStream is1 = new SubProcess.SubInputStream();
			SubProcess.SubInputStream es1 = new SubProcess.SubInputStream();

			long[] la = new long[4];
			createProcess0(cmdarray, envp, dir, la);
			if(la[0] == 0)
			{
				String cmd = null;
				for(int i = 0; i < cmdarray.length; i++)
				{
					if(i == 0)
					{
						cmd = "\"" + cmdarray[i] + "\"";
					}
					else
					{
						cmd = cmd + " " + cmdarray[i];
					}
				}
				throw new IOException("The creation of the Process has just failed: " + cmd);
			}
			this.processHandle = (int) la[0];
			os1.streamHandle = la[1];
			is1.streamHandle = la[2];
			es1.streamHandle = la[3];
			os = new BufferedOutputStream(os1);
			is = new BufferedInputStream(is1);
			es = new BufferedInputStream(es1);
		}

		/**
		 * @seeint waitFor() from Process
		 */
		public int waitFor() throws InterruptedException
		{
			while(true)
			{
				synchronized(this)
				{
					if(getState0(processHandle))
						break;
				}
				Thread.sleep(50);
			}

			return processExitCode;
		}

		/**
		 * @see int exitValue() from Process
		 */
		public synchronized int exitValue() throws IllegalThreadStateException
		{
			if(!getState0(processHandle))
			{
				throw new IllegalThreadStateException("process has not exited");
			}

			return processExitCode;
		}

		/**
		 * @see destroy() from Process
		 */
		private final native void destroy0(int thisProcessHandle);

		public synchronized final void destroy()
		{
			destroy0(processHandle);
		}

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////     RUNTIME     BODY     ////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * "Every Java application has a single instance of class Runtime ..."
	 */
	private static Runtime thisApplicationRuntime = new Runtime();

	private static ArrayList<Thread> hooksList = new ArrayList<Thread>();

	/**
	 * 0 - normal work
	 * 1 - being shutdown sequence running
	 * 2 - being finalizing
	 */
	private static int VMState = 0;

	static boolean finalizeOnExit = false;

	/**
	 * An application cannot create its own instance of this class.
	 */
	private Runtime()
	{
	}


	/**
	 * Executes the specified command and its arguments in a separate platform
	 * process. The new process inherits the environment of the caller. Calling
	 * this method is equivalent to calling {@code exec(progArray, null, null)}.
	 *
	 * @param progArray the array containing the program to execute as well as any
	 *                  arguments to the program.
	 * @return the new {@code Process} object that represents the platform
	 *         process.
	 * @throws java.io.IOException if the requested program can not be executed.
	 * @throws SecurityException   if the current {@code SecurityManager} disallows program
	 *                             execution.
	 * @see SecurityManager#checkExec
	 */
	public Process exec(String[] cmdarray) throws IOException
	{
		return exec(cmdarray, null, null);
	}

	/**
	 * Executes the specified command and its arguments in a separate platform
	 * process. The new process uses the environment provided in {@code envp}.
	 * Calling this method is equivalent to calling
	 * {@code exec(progArray, envp, null)}.
	 *
	 * @param progArray the array containing the program to execute as well as any
	 *                  arguments to the program.
	 * @param envp      the array containing the environment to start the new process
	 *                  in.
	 * @return the new {@code Process} object that represents the platform
	 *         process.
	 * @throws java.io.IOException if the requested program can not be executed.
	 * @throws SecurityException   if the current {@code SecurityManager} disallows program
	 *                             execution.
	 * @see SecurityManager#checkExec
	 */
	public Process exec(String[] cmdarray, String[] envp) throws IOException, NullPointerException, IndexOutOfBoundsException, SecurityException
	{
		return exec(cmdarray, envp, null);

	}

	/**
	 * Executes the specified command and its arguments in a separate platform
	 * process. The new process uses the environment provided in {@code envp}
	 * and the working directory specified by {@code directory}.
	 *
	 * @param progArray the array containing the program to execute as well as any
	 *                  arguments to the program.
	 * @param envp      the array containing the environment to start the new process
	 *                  in.
	 * @param directory the directory in which to execute the program. If {@code null},
	 *                  execute if in the same directory as the parent process.
	 * @return the new {@code Process} object that represents the platform
	 *         process.
	 * @throws java.io.IOException if the requested program can not be executed.
	 * @throws SecurityException   if the current {@code SecurityManager} disallows program
	 *                             execution.
	 * @see SecurityManager#checkExec
	 */
	public Process exec(String[] cmdarray, String[] envp, File dir) throws IOException
	{
		SecurityManager currentSecurity = System.getSecurityManager();

		if(currentSecurity != null)
		{
			currentSecurity.checkExec(cmdarray[0]);
		}

		if(cmdarray == null)
		{
			throw new NullPointerException("Command argument shouldn't be empty.");
		}
		if(cmdarray.length == 0)
		{
			throw new IndexOutOfBoundsException();
		}
		for(int i = 0; i < cmdarray.length; i++)
		{
			if(cmdarray[i] == null)
			{
				throw new NullPointerException("An element of cmdarray shouldn't be empty.");
			}
		}
		if(envp != null)
		{
			if(envp.length != 0)
			{
				for(int i = 0; i < envp.length; i++)
				{
					if(envp[i] == null)
					{
						throw new NullPointerException("An element of envp shouldn't be empty.");
					}
				}
			}
			else
			{
				envp = null;
			}
		}

		String dirPathName = (dir != null ? dir.getPath() : null);

		SubProcess sp = new SubProcess();

		sp.execVM(cmdarray, envp, dirPathName);

		return sp;

	}

	/**
	 * Executes the specified program in a separate platform process. The new
	 * process inherits the environment of the caller. Calling this method is
	 * equivalent to calling {@code exec(prog, null, null)}.
	 *
	 * @param prog the name of the program to execute.
	 * @return the new {@code Process} object that represents the platform
	 *         process.
	 * @throws java.io.IOException if the requested program can not be executed.
	 * @throws SecurityException   if the current {@code SecurityManager} disallows program
	 *                             execution.
	 * @see SecurityManager#checkExec
	 */
	public Process exec(String command) throws IOException
	{
		return exec(command, null, null);
	}

	/**
	 * Executes the specified program in a separate platform process. The new
	 * process uses the environment provided in {@code envp}. Calling this
	 * method is equivalent to calling {@code exec(prog, envp, null)}.
	 *
	 * @param prog the name of the program to execute.
	 * @param envp the array containing the environment to start the new process
	 *             in.
	 * @return the new {@code Process} object that represents the platform
	 *         process.
	 * @throws java.io.IOException if the requested program can not be executed.
	 * @throws SecurityException   if the current {@code SecurityManager} disallows program
	 *                             execution.
	 * @see SecurityManager#checkExec
	 */
	public Process exec(String cmd, String[] envp) throws IOException
	{
		return exec(cmd, envp, null);
	}

	/**
	 * Executes the specified program in a separate platform process. The new
	 * process uses the environment provided in {@code envp} and the working
	 * directory specified by {@code directory}.
	 *
	 * @param prog      the name of the program to execute.
	 * @param envp      the array containing the environment to start the new process
	 *                  in.
	 * @param directory the directory in which to execute the program. If {@code null},
	 *                  execute if in the same directory as the parent process.
	 * @return the new {@code Process} object that represents the platform
	 *         process.
	 * @throws java.io.IOException if the requested program can not be executed.
	 * @throws SecurityException   if the current {@code SecurityManager} disallows program
	 *                             execution.
	 * @see SecurityManager#checkExec
	 */
	public Process exec(String command, String[] envp, File dir) throws IOException
	{
		if(command == null)
		{
			throw new NullPointerException();
		}
		if(command.length() == 0)
		{
			throw new IllegalArgumentException();
		}
		if(envp != null)
		{
			if(envp.length != 0)
			{
				for(int i = 0; i < envp.length; i++)
				{
					if(envp[i] == null)
					{
						throw new NullPointerException("An element of envp shouldn't be empty.");
					}
				}
			}
			else
			{
				envp = null;
			}
		}

		StringTokenizer st = new StringTokenizer(command);
		String[] cmdarray = new String[st.countTokens()];
		int i = 0;

		while(st.hasMoreTokens())
		{
			cmdarray[i++] = st.nextToken();

		}

		return exec(cmdarray, envp, dir);

	}

	/**
	 * Causes the virtual machine to stop running and the program to exit. If
	 * {@link #runFinalizersOnExit(boolean)} has been previously invoked with a
	 * {@code true} argument, then all objects will be properly
	 * garbage-collected and finalized first.
	 *
	 * @param code the return code. By convention, non-zero return codes indicate
	 *             abnormal terminations.
	 * @throws SecurityException if the current {@code SecurityManager} does not allow the
	 *                           running thread to terminate the virtual machine.
	 * @see SecurityManager#checkExit
	 */
	public void exit(int status) throws SecurityException
	{
		SecurityManager sm = System.getSecurityManager();
		if(sm != null)
		{
			sm.checkExit(status);
		}
		// Halt the VM if it is running finalizers.
		if(VMState == 2 && finalizeOnExit == true && status != 0)
		{
			halt(status);
		}

		execShutdownSequence();
		// No need to invoke finalizers one more time.
		//                             vvvvv
		VMExecutionEngine.exit(status, false);
	}

	/**
	 * Returns the amount of free memory resources which are available to the
	 * running program.
	 *
	 * @return the approximate amount of free memory, measured in bytes.
	 */
	public long freeMemory()
	{
		return VMMemoryManager.getFreeMemory();
	}

	/**
	 * Indicates to the virtual machine that it would be a good time to run the
	 * garbage collector. Note that this is a hint only. There is no guarantee
	 * that the garbage collector will actually be run.
	 */
	public void gc()
	{
		VMMemoryManager.runGC();
	}

	/**
	 * Returns the single {@code Runtime} instance.
	 *
	 * @return the {@code Runtime} object for the current application.
	 */
	public static Runtime getRuntime()
	{
		return thisApplicationRuntime;
	}

	/**
	 * Loads and links the dynamic library that is identified through the
	 * specified path. This method is similar to {@link #loadLibrary(String)},
	 * but it accepts a full path specification whereas {@code loadLibrary} just
	 * accepts the name of the library to load.
	 *
	 * @param pathName the absolute (platform dependent) path to the library to load.
	 * @throws UnsatisfiedLinkError if the library can not be loaded.
	 * @throws SecurityException    if the current {@code SecurityManager} does not allow to load
	 *                              the library.
	 * @see SecurityManager#checkLink
	 */
	public void load(String filename) throws SecurityException, UnsatisfiedLinkError
	{
		load0(filename, VMClassRegistry.getClassLoader(VMStack.getCallerClass(0)), true);
	}

	void load0(String filename, ClassLoader cL, boolean check) throws SecurityException, UnsatisfiedLinkError
	{
		if(check)
		{
			if(filename == null)
			{
				throw new NullPointerException();
			}

			SecurityManager currentSecurity = System.getSecurityManager();

			if(currentSecurity != null)
			{
				currentSecurity.checkLink(filename);
			}
		}
		VMClassRegistry.loadLibrary(filename, cL); // Should throw UnsatisfiedLinkError if needs.
	}

	/**
	 * Loads and links the library with the specified name. The mapping of the
	 * specified library name to the full path for loading the library is
	 * implementation-dependent.
	 *
	 * @param libName the name of the library to load.
	 * @throws UnsatisfiedLinkError if the library can not be loaded.
	 * @throws SecurityException    if the current {@code SecurityManager} does not allow to load
	 *                              the library.
	 * @see SecurityManager#checkLink
	 */
	public void loadLibrary(String libname) throws SecurityException, UnsatisfiedLinkError
	{
		loadLibrary0(libname, VMClassRegistry.getClassLoader(VMStack.getCallerClass(0)), true);
	}

	void loadLibrary0(String libname, ClassLoader cL, boolean check) throws SecurityException, UnsatisfiedLinkError
	{
		if(check)
		{
			if(libname == null)
			{
				throw new NullPointerException();
			}

			SecurityManager currentSecurity = System.getSecurityManager();

			if(currentSecurity != null)
			{
				currentSecurity.checkLink(libname);
			}
		}

		String libFullName = null;

		if(cL != null)
		{
			libFullName = cL.findLibrary(libname);
		}
		if(libFullName == null)
		{
			String allPaths = null;

			//XXX: should we think hard about security policy for this block?:
			String jlp = System.getProperty("java.library.path");
			String vblp = System.getProperty("vm.boot.library.path");
			String udp = System.getProperty("user.dir");
			String pathSeparator = System.getProperty("path.separator");
			String fileSeparator = System.getProperty("file.separator");
			allPaths = (jlp != null ? jlp : "") + (vblp != null ? pathSeparator + vblp : "") + (udp != null ? pathSeparator + udp : "");

			if(allPaths.length() == 0)
			{
				throw new UnsatisfiedLinkError("Can not find the library: " + libname);
			}

			//String[] paths = allPaths.split(pathSeparator);
			String[] paths;
			{
				ArrayList<String> res = new ArrayList<String>();
				int curPos = 0;
				int l = pathSeparator.length();
				int i = allPaths.indexOf(pathSeparator);
				int in = 0;
				while(i != -1)
				{
					String s = allPaths.substring(curPos, i);
					res.add(s);
					in++;
					curPos = i + l;
					i = allPaths.indexOf(pathSeparator, curPos);
				}

				if(curPos <= allPaths.length())
				{
					String s = allPaths.substring(curPos, allPaths.length());
					in++;
					res.add(s);
				}

				paths = (String[]) res.toArray(new String[in]);
			}

			libname = System.mapLibraryName(libname);
			for(int i = 0; i < paths.length; i++)
			{
				if(paths[i] == null)
				{
					continue;
				}
				libFullName = paths[i] + fileSeparator + libname;
				try
				{
					this.load0(libFullName, cL, false);
					return;
				}
				catch(UnsatisfiedLinkError e)
				{
				}
			}
		}
		else
		{
			this.load0(libFullName, cL, false);
			return;
		}
		throw new UnsatisfiedLinkError("Can not find the library: " + libname);
	}

	/**
	 * Provides a hint to the virtual machine that it would be useful to attempt
	 * to perform any outstanding object finalizations.
	 */
	public void runFinalization()
	{
		VMMemoryManager.runFinalization();
	}

	/**
	 * Sets the flag that indicates whether all objects are finalized when the
	 * virtual machine is about to exit. Note that all finalization which occurs
	 * when the system is exiting is performed after all running threads have
	 * been terminated.
	 *
	 * @param run {@code true} to enable finalization on exit, {@code false} to
	 *            disable it.
	 * @deprecated This method is unsafe.
	 */
	@Deprecated
	public static void runFinalizersOnExit(boolean value)
	{
		SecurityManager sm = System.getSecurityManager();
		if(sm != null)
		{
			sm.checkExit(0);
		}
		synchronized(hooksList)
		{
			finalizeOnExit = value;
		}
	}

	/**
	 * Returns the total amount of memory which is available to the running
	 * program.
	 *
	 * @return the total amount of memory, measured in bytes.
	 */
	public long totalMemory()
	{
		return VMMemoryManager.getTotalMemory();
	}

	/**
	 * Switches the output of debug information for instructions on or off.
	 *
	 * @param enable {@code true} to switch tracing on, {@code false} to switch it
	 *               off.
	 */
	public void traceInstructions(boolean on)
	{
		VMExecutionEngine.traceInstructions(on);
	}

	/**
	 * Switches the output of debug information for methods on or off.
	 *
	 * @param enable {@code true} to switch tracing on, {@code false} to switch it
	 *               off.
	 */
	public void traceMethodCalls(boolean on)
	{
		VMExecutionEngine.traceMethodCalls(on);
	}

	/**
	 * Returns the localized version of the specified input stream. The input
	 * stream that is returned automatically converts all characters from the
	 * local character set to Unicode after reading them from the underlying
	 * stream.
	 *
	 * @param stream the input stream to localize.
	 * @return the localized input stream.
	 * @deprecated Use {@link java.io.InputStreamReader}.
	 */
	@Deprecated
	public InputStream getLocalizedInputStream(InputStream stream)
	{
		return stream;
	}

	/**
	 * Returns the localized version of the specified output stream. The output
	 * stream that is returned automatically converts all characters from
	 * Unicode to the local character set before writing them to the underlying
	 * stream.
	 *
	 * @param stream the output stream to localize.
	 * @return the localized output stream.
	 * @deprecated Use {@link java.io.OutputStreamWriter}.
	 */
	@Deprecated
	public OutputStream getLocalizedOutputStream(OutputStream stream)
	{
		return stream;
	}

	/**
	 * Registers a virtual-machine shutdown hook. A shutdown hook is a
	 * {@code Thread} that is ready to run, but has not yet been started. All
	 * registered shutdown hooks will be executed once the virtual machine shuts
	 * down properly. A proper shutdown happens when either the
	 * {@link #exit(int)} method is called or the surrounding system decides to
	 * terminate the application, for example in response to a {@code CTRL-C} or
	 * a system-wide shutdown. A termination of the virtual machine due to the
	 * {@link #halt(int)} method, an {@link Error} or a {@code SIGKILL}, in
	 * contrast, is not considered a proper shutdown. In these cases the
	 * shutdown hooks will not be run.
	 * <p/>
	 * Shutdown hooks are run concurrently and in an unspecified order. Hooks
	 * failing due to an unhandled exception are not a problem, but the stack
	 * trace might be printed to the console. Once initiated, the whole shutdown
	 * process can only be terminated by calling {@code halt()}.
	 * <p/>
	 * If {@link #runFinalizersOnExit(boolean)} has been called with a {@code
	 * true} argument, garbage collection and finalization will take place after
	 * all hooks are either finished or have failed. Then the virtual machine
	 * terminates.
	 * <p/>
	 * It is recommended that shutdown hooks do not do any time-consuming
	 * activities, in order to not hold up the shutdown process longer than
	 * necessary.
	 *
	 * @param hook the shutdown hook to register.
	 * @throws IllegalArgumentException if the hook has already been started or if it has already
	 *                                  been registered.
	 * @throws IllegalStateException    if the virtual machine is already shutting down.
	 * @throws SecurityException        if a SecurityManager is registered and the calling code
	 *                                  doesn't have the RuntimePermission("shutdownHooks").
	 */
	public void addShutdownHook(Thread hook)
	{
		SecurityManager sm = System.getSecurityManager();
		if(sm != null)
		{
			sm.checkPermission(RuntimePermissionCollection.SHUTDOWN_HOOKS_PERMISSION);
		}
		// Check hook for null
		if(hook == null)
			throw new NullPointerException("null is not allowed here");

		if(hook.getState() != Thread.State.NEW)
		{
			throw new IllegalArgumentException();
		}
		if(VMState > 0)
		{
			throw new IllegalStateException();
		}
		synchronized(hooksList)
		{
			if(hooksList.contains(hook))
			{
				throw new IllegalArgumentException();
			}
			hooksList.add(hook);
		}
	}

	/**
	 * Unregisters a previously registered virtual machine shutdown hook.
	 *
	 * @param hook the shutdown hook to remove.
	 * @return {@code true} if the hook has been removed successfully; {@code
	 *         false} otherwise.
	 * @throws IllegalStateException if the virtual machine is already shutting down.
	 * @throws SecurityException     if a SecurityManager is registered and the calling code
	 *                               doesn't have the RuntimePermission("shutdownHooks").
	 */
	public boolean removeShutdownHook(Thread hook)
	{
		SecurityManager sm = System.getSecurityManager();
		if(sm != null)
		{
			sm.checkPermission(RuntimePermissionCollection.SHUTDOWN_HOOKS_PERMISSION);
		}
		// Check hook for null
		if(hook == null)
			throw new NullPointerException("null is not allowed here");

		if(VMState > 0)
		{
			throw new IllegalStateException();
		}
		synchronized(hooksList)
		{
			return hooksList.remove(hook);
		}
	}

	/**
	 * Causes the virtual machine to stop running, and the program to exit.
	 * Neither shutdown hooks nor finalizers are run before.
	 *
	 * @param code the return code. By convention, non-zero return codes indicate
	 *             abnormal terminations.
	 * @throws SecurityException if the current {@code SecurityManager} does not allow the
	 *                           running thread to terminate the virtual machine.
	 * @see SecurityManager#checkExit
	 * @see #addShutdownHook(Thread)
	 * @see #removeShutdownHook(Thread)
	 * @see #runFinalizersOnExit(boolean)
	 */
	public void halt(int status)
	{
		SecurityManager sm = System.getSecurityManager();

		if(sm != null)
		{
			sm.checkExit(status);
		}
		VMExecutionEngine.exit(status, false);
	}

	/**
	 * Returns the number of processors available to the virtual machine.
	 *
	 * @return the number of available processors, at least 1.
	 */
	public int availableProcessors()
	{
		return VMExecutionEngine.getAvailableProcessors();
	}

	/**
	 * Returns the maximum amount of memory that may be used by the virtual
	 * machine, or {@code Long.MAX_VALUE} if there is no such limit.
	 *
	 * @return the maximum amount of memory that the virtual machine will try to
	 *         allocate, measured in bytes.
	 */
	public long maxMemory()
	{
		return VMMemoryManager.getMaxMemory();
	}


	void execShutdownSequence()
	{
		synchronized(hooksList)
		{
			if(VMState > 0)
			{
				return;
			}
			try
			{
				// Phase1: Execute all registered hooks.
				VMState = 1;
				for(Thread hook : hooksList)
				{
					hook.start();
				}

				for(Thread hook : hooksList)
				{
					while(true)
					{
						try
						{
							hook.join();
							break;
						}
						catch(InterruptedException e)
						{
							continue;
						}
					}
				}
				// Phase2: Execute all finalizers if nessesary.
				VMState = 2;
				FinalizerThread.shutdown(finalizeOnExit);

				// Close connections.
				if(VM.closeJars)
				{
					JarURLConnectionImpl.closeCachedFiles();
				}

				// Delete files.
				if(VM.deleteOnExit)
				{
					DeleteOnExit.deleteOnExit();
				}
			}
			catch(Throwable e)
			{
				// just catch all exceptions
			}
		}
	}
}

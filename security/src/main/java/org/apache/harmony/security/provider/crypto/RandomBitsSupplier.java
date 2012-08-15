/*
 * Copyright 2010-2012 napile.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.harmony.security.provider.crypto;

import org.napile.annotation.dev.Dummy;

/**
 * @author VISTALL
 * @date 22:36/14.08.12
 */
@Dummy
public class RandomBitsSupplier
{
	public static boolean isServiceAvailable()
	{
		throw new UnsupportedOperationException();
	}

	public static byte[] getRandomBits(int numBytes)
	{
		throw new UnsupportedOperationException();
	}
}

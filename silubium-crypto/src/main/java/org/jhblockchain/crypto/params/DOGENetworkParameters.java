/*
 * Copyright 2011 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jhblockchain.crypto.params;

import lombok.Data;
import org.bitcoinj.params.MainNetParams;

/**
 * <p>NetworkParameters contains the data needed for working with an instantiation of a Bitcoin chain.</p>
 *
 * <p>This is an abstract class, concrete instantiations can be found in the params package. There are four:
 * one for the main network ({@link MainNetParams}), one for the public test network, and two others that are
 * intended for unit testing and local app development purposes. Although this class contains some aliases for
 * them, you are encouraged to call the static get() methods on each specific params class directly.</p>
 */
@Data
public class DOGENetworkParameters extends NetworkParameters{

    /**
     * base58Prefixes[PUBKEY_ADDRESS]
     */
    private int TEST_NET_ADDRESS_SUFFIX = 0x71;
    private int MAIN_NET_ADDRESS_SUFFIX = 0x1e;

    /**
     *base58Prefixes[SECRET_KEY]
     */
    private int MAIN_NET_PRIVATE_KEY_PREFIX = 0x9e;
    private int TEST_NET_PRIVATE_KEY_PREFIX = 0xf1;


    private int MAIN_NET_PRIVATE_KEY_SUFFIX = 0x01;

    private int RAW_PRIVATE_KEY_COMPRESSED_LENGTH = 38;
    private int RAW_PRIVATE_KEY_NO_COMPRESSED_LENGTH = 37;

}

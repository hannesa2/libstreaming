/*
 * Copyright (C) 2011-2015 GUIGUI Simon, fyhertz@gmail.com
 *
 * This file is part of libstreaming (https://github.com/fyhertz/libstreaming)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.majorkernelpanic.streaming

import java.io.IOException
import java.io.OutputStream
import java.net.InetAddress

/**
 * An interface that represents a Stream.
 */
interface Stream {
    /**
     * Configures the stream. You need to call this before calling [.getSessionDescription]
     * to apply your configuration of the stream.
     */
    @Throws(IllegalStateException::class, IOException::class)
    fun configure()

    /**
     * Starts the stream.
     * This method can only be called after [Stream.configure].
     */
    @Throws(IllegalStateException::class, IOException::class)
    fun start()

    /**
     * Stops the stream.
     */
    fun stop()

    /**
     * Sets the Time To Live of packets sent over the network.
     * @param ttl The time to live
     * @throws IOException
     */
    @Throws(IOException::class)
    fun setTimeToLive(ttl: Int)

    /**
     * Sets the destination ip address of the stream.
     * @param dest The destination address of the stream
     */
    fun setDestinationAddress(dest: InetAddress?)

    /**
     * Sets the destination ports of the stream.
     * If an odd number is supplied for the destination port then the next
     * lower even number will be used for RTP and it will be used for RTCP.
     * If an even number is supplied, it will be used for RTP and the next odd
     * number will be used for RTCP.
     * @param dport The destination port
     */
    fun setDestinationPorts(dport: Int)

    /**
     * Sets the destination ports of the stream.
     * @param rtpPort Destination port that will be used for RTP
     * @param rtcpPort Destination port that will be used for RTCP
     */
    fun setDestinationPorts(rtpPort: Int, rtcpPort: Int)

    /**
     * If a TCP is used as the transport protocol for the RTP session,
     * the output stream to which RTP packets will be written to must
     * be specified with this method.
     */
    fun setOutputStream(stream: OutputStream?, channelIdentifier: Byte)

    /**
     * Returns a pair of source ports, the first one is the
     * one used for RTP and the second one is used for RTCP.
     */
    val localPorts: IntArray?

    /**
     * Returns a pair of destination ports, the first one is the
     * one used for RTP and the second one is used for RTCP.
     */
    fun getDestinationPorts(): IntArray?

    /**
     * Returns the SSRC of the underlying [net.majorkernelpanic.streaming.rtp.RtpSocket].
     * @return the SSRC of the stream.
     */
    val sSRC: Int

    /**
     * Returns an approximation of the bit rate consumed by the stream in bit per seconde.
     */
    val bitrate: Long

    /**
     * Returns a description of the stream using SDP.
     * This method can only be called after [Stream.configure].
     * @throws IllegalStateException Thrown when [Stream.configure] wa not called.
     */
    @get:Throws(IllegalStateException::class)
    val sessionDescription: String?
    val isStreaming: Boolean
}
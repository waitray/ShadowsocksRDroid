package com.proxy.shadowsocksr.impl.crypto.crypto

import com.proxy.shadowsocksr.impl.ImplUtils
import org.spongycastle.crypto.StreamCipher
import org.spongycastle.crypto.engines.RC4Engine
import org.spongycastle.crypto.params.KeyParameter

class RC4MD5Crypto(cryptMethod: String, key: ByteArray) : AbsCrypto(cryptMethod, key)
{
    private val e: StreamCipher = RC4Engine()
    private val d: StreamCipher = RC4Engine()

    //rc4-md5 spec: https://github.com/shadowsocks/shadowsocks/issues/178
    override fun updateEncryptIV(iv: ByteArray)
    {
        var bts = ByteArray(key.size + iv.size)
        System.arraycopy(key, 0, bts, 0, key.size)
        System.arraycopy(iv, 0, bts, key.size, iv.size)
        bts = ImplUtils.getMD5(bts)
        e.init(true, KeyParameter(bts))
    }

    override fun updateDecryptIV(iv: ByteArray)
    {
        var bts = ByteArray(key.size + iv.size)
        System.arraycopy(key, 0, bts, 0, key.size)
        System.arraycopy(iv, 0, bts, key.size, iv.size)
        bts = ImplUtils.getMD5(bts)
        d.init(false, KeyParameter(bts))
    }

    override fun encrypt(data: ByteArray): ByteArray
    {
        e.processBytes(data, 0, data.size, data, 0)
        return data
    }

    override fun decrypt(data: ByteArray): ByteArray
    {
        d.processBytes(data, 0, data.size, data, 0)
        return data
    }
}
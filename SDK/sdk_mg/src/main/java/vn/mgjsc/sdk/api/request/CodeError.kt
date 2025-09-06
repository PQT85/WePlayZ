package vn.mgjsc.sdk.api.request

import java.lang.Exception

/**
 * Create by weed songpq on 21/11/2019.
 */
class CodeError(var code: Int, message: String) : Exception(message) {
}
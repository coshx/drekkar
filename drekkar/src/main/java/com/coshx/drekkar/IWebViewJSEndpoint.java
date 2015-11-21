package com.coshx.drekkar;

/**
 * @class IWebViewJSEndpoint
 * @brief
 */
interface IWebViewJSEndpoint {
    void handle(String busName, String eventName, String data);
}

package com.coshx.drekkar;

/**
 * @class IWebViewJSEndpoint
 * @brief
 */
interface IWebViewJSEndpoint {
    void onMessage(String busName, String eventName, String rawData);
}

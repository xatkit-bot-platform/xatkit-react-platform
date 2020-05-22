# Changelog

All notable changes for the React platform will be documented in this file.

Note that there is no changelog available for the initial release of the platform (2.0.0), you can find the release notes [here](https://github.com/xatkit-bot-platform/xatkit-react-platform/releases).

The changelog format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/v2.0.0.html)

## Unreleased

## Added

- Configuration property `xatkit.react.public_url` to specify the path of the socket.io server. This property is used when accessing the bot using the `/admin` url, and allows to tune the HTML generation to connect to a specific react server. The provided URL must contain the hostname, port, and path to the react server, and can be used in nginx configuration for URL rewriting. If not specified the `/admin` HTML page will point to `http://localhost:<react port>`.
- A set of `RestHandler` mimicking the behavior of the `/admin` endpoint previously defined in *xatkit-runtime* (see [#237](https://github.com/xatkit-bot-platform/xatkit-runtime/issues/237) for more information). The testing page can still be accessed at `localhost:5000/admin` (or the public URL specified in the configuration), and behave as before. **Note that these endpoints are only accessible when using the RestPlatform**.
- Configuration property `xatkit.react.enable_testing_page` to specify if the testing page (located at `/admin`) should be enabled or not. This property can be set to `false` if a bot is embedded in an existing website, and should not be available through another URL. The default value for this property is `true`.
- Support for *quick message buttons* (fix [#5](https://github.com/xatkit-bot-platform/xatkit-react-platform/issues/5)): the `PostMessage` and `Reply` actions can now print buttons to drive the conversation. The values of these buttons are specified in a new parameter `buttons` of the action. This value is used as a regular user input and is matched to existing intents. **This change does not break the public API**: `PostMessage` and `Reply` actions still provide a constructor without buttons.
- `PostMessage` now implements `RuntimeArtifactAction#beforeDelay`, and sends an event to the web client to print loading dots. Note that loading dots are not displayed if the provided delay is *0*. The loading dots are **always** removed after displaying a new bot message, the server needs to send another event to display them again.
- Action `ToggleDarkMode` that notifies the client to enable/disable dark mode.
- Action `Wait` that pauses the execution.
- Action `ReplyLinkSnippet` to tell the UI to display a link snippet with a preview image.

### Changed

- The default value of `xatkit.react.client.url` is now `*`: this eases the deployment in development / test environment. Note that production deployment **should** specify a value for this property in order to restrict the access to the deployed bot.
- The `/admin` endpoint is no longer available if the Xatkit configuration contains the property `xatkit.react.enable_testing_page = false`.
- Change log level of non-critical message from the internal socket.io server. This reduces the amount of noise in Xatkit logs.
- Events `Client_Ready` and `Client_Closed` create empty contexts `react_ready` and `react_closed` in addition to the `react` context. These contexts can be used to define intents following the conversation start.
- Event `Client_Ready` now defines additional parameters in the `react` context: `react.hostname`, `react.url`, and `react.origin` containing information related to the page where the bot is located.

### Fixed

- [#9](https://github.com/xatkit-bot-platform/xatkit-react-platform/issues/9): *Change log level to trace/debug for socket.io server logs*
- [#12](https://github.com/xatkit-bot-platform/xatkit-react-platform/issues/12): *Add an action to toggle dark mode*

## [4.0.0] - 2019-12-01

### Added

- Configuration property `xatkit.react.client.url` to specify the URL of client domains embedding the react chat component.  This property is translated into a `Access-Control-Allow-Origin` header that tells the browser to authorize requests from the specified domain. Supported values for the property are single URL and wildcard (`"*"`). The standard does not authorize multiple URLs in the `Access-Control-Allow-Origin` header (see [here]( https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Origin )).
- `ReactEventProvider` that fires non-textual events related to the react client. The provider currently provides two events: `Client_Ready` and `Client_Closed`, that are triggered when a new client connects to/disconnects from Xatkit. These events set context parameters allowing to use the `Reply` action as a response. See the [wiki]( https://github.com/xatkit-bot-platform/xatkit-releases/wiki/Xatkit-React-Platform#reacteventprovider-events) for more information.
- Support for bots hosted on `https` domains. The Xatkit configuration can now contain a `xatkit.server.ssl.keystore` property specifying the location of the keystore to use to sign http responses. Additional properties specifying keystore password are also required, check [Xatkit configuration options](https://github.com/xatkit-bot-platform/xatkit-releases/wiki/Xatkit-Options) for more information. 

### Changed
- Action parameters and return are now statically typed. **This change breaks the public API**: execution models relying on the generic `Object` type for parameter and return now need to cast values to the expected type. (e.g. `ChatPlatform.Reply(message)` now requires that `message` is a `String`, this can be fixed with the following syntax `ChatPlatform.Reply(message as String)`).  

## [3.0.0]- 2019-10-10

### Added

- Actions `ReplyFileMessage(file)` and `ReplyFileMessage(file, message)` to post messages containing file through the react component. These actions upload the provided file on Xatkit's public directory (see [this commit on xatkit-runtime](https://github.com/xatkit-bot-platform/xatkit-runtime/commit/cdb0521320e2606a4ae3a5e4c12618ad018afaf8)). If the file is an image (`.gif`, `.jpg`, or `.png`) the react component prints a clickable thumbnail of the image, otherwise a link is created to open the file or download it.
- `ReactPlatform.shutdown()` is now automatically called on JVM shutdown. This ensures the the SocketIO server has been properly stopped.

### Changed

- `ReactIntentProvider` and `ChatProvider` now use the new intent provider hierarchy (see [xatkit-runtime/#221](https://github.com/xatkit-bot-platform/xatkit-runtime/issues/221)).
- React platform's intent providers and actions now use [socket.io](https://socket.io/) to receive messages and reply in real-time. **This change breaks the public API**: the REST endpoint `/react/getAnswers` doesn't exist anymore, and messages must be passed as JSON objects through the socket).
- The SocketIO server now allows address reuse by default: this allows to quickly restart Xatkit and reuse the same port without binding errors.

## [2.0.0] - 2019-08-20 

See the release notes [here](https://github.com/xatkit-bot-platform/xatkit-react-platform/releases).

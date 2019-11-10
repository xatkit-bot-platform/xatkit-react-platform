Xatkit React Platform
=====

[![License Badge](https://img.shields.io/badge/license-EPL%202.0-brightgreen.svg)](https://opensource.org/licenses/EPL-2.0)
[![Build Status](https://travis-ci.com/xatkit-bot-platform/xatkit-react-platform.svg?branch=master)](https://travis-ci.com/xatkit-bot-platform/xatkit-react-platform)
[![Wiki Badge](https://img.shields.io/badge/doc-wiki-blue)](https://github.com/xatkit-bot-platform/xatkit-releases/wiki/Xatkit-React-Platform)

Receive and send messages from our [React-based chat component](https://github.com/xatkit-bot-platform/xatkit-react). This platform is bundled with the [Xatkit release](https://github.com/xatkit-bot-platform/xatkit-releases/releases).

The React platform is a concrete implementation of the [*ChatPlatform*](https://github.com/xatkit-bot-platform/xatkit-chat-platform).

## Providers

The React platform defines the following providers:

| Provider                   | Type  | Context Parameters | Description                                                  |
| -------------------------- | ----- | ------------------ | ------------------------------------------------------------ |
| ChatProvider | Intent | - `chat.channel`: the identifier of the channel that sent the message<br/> - `chat.username`: the name of the user that sent the message<br/> - `chat.rawMessage`: the raw message sent by the user (before NLP processing) | Receive messages from a communication channel and translate them into Xatkit-compatible intents (*inherited from [ChatPlatform](https://github.com/xatkit-bot-platform/xatkit-chat-platform)*) |
| ReactIntentProvider | Intent | - `react.channel`: the identifier of the react channel that sent the message<br/> - `react.username`: the name of the react user that sent the message<br/> - `react.rawMessage`: the raw message sent by the user (before NLP processing)<br/> | Receive messages from the react component and translates them into Xatkit-compatible intents. Note that `react.channel`, `react.username`, and `react.rawMessage` contain the same values as `chat.channel`, `chat.username`, and `chat.rawMessage` |

## Actions

| Action | Parameters                                                   | Return                         | Return Type | Description                                                 |
| ------ | ------------------------------------------------------------ | ------------------------------ | ----------- | ----------------------------------------------------------- |
| PostMessage | - `message`(**String**): the message to post<br/> - `channel` (**String**): the identfier of the react channel to post the message to | The posted message | String | Posts the provided `message` to the given react `channel` (*inherited from [ChatPlatform](https://github.com/xatkit-bot-platform/xatkit-chat-platform)*) |
| Reply | - `message` (**String**): the message to post as a reply | The posted message | String | Posts the provided `message` as a reply to a received message (*inherited from [ChatPlatform](https://github.com/xatkit-bot-platform/xatkit-chat-platform)*) |
| ItemizeList | - `list` ([**List**](https://docs.oracle.com/javase/7/docs/api/java/util/List.html)): the list to itemize | A String presenting the provided `list` as a set of items | String | Creates a set of items from the provided `list`. This actions relies on `Object.toString()` to print each item's content |
| ItemizeList | - `list` ([**List**](https://docs.oracle.com/javase/7/docs/api/java/util/List.html)): the list to itemize<br/> - `formatter` ([**Formatter**](https://xatkit-bot-platform.github.io/xatkit-runtime-docs/releases/snapshot/doc/com/xatkit/core/platform/Formatter.html) the formatter used to print each item | A String presenting the provided `list` as a set of items formatted with the given `formatter` | String | Creates a set of items from the provided `list`. This action relies on the provided `formatter` to print each item's content |
| EnumerateList | - `list` ([**List**](https://docs.oracle.com/javase/7/docs/api/java/util/List.html)): the list to enumerate | A String presenting the provided `list` as an enumeration | String | Creates an enumeration from the provided `list`. This actions relies on `Object.toString()` to print each item's content |
| EnumerateList | - `list` ([**List**](https://docs.oracle.com/javase/7/docs/api/java/util/List.html)): the list to enumerate<br/> - `formatter` ([**Formatter**](https://xatkit-bot-platform.github.io/xatkit-runtime-docs/releases/snapshot/doc/com/xatkit/core/platform/Formatter.html) the formatter used to print each item | A String presenting the provided `list` as an enumeration formatted with the given `formatter` | String | Creates an enumeration from the provided `list`. This action relies on the provided `formatter` to print each item's content |

## Options

The React platform supports the following configuration options

| Key                       | Values  | Description                                                  | Constraint                                                   |
| ------------------------- | ------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `xatkit.react.client.url` | String  | The base URL of the website displaying the react chat component. Supports wildcard (`"*"`) to allow any domain. | **Optional** (default to `<xatkit.server.public_url>:<xatkit.server.port>`) |
| `xatkit.react.port`       | Integer | The port used to start the socket server used by the React platform. | **Optional** (default `5001`)                                |

**Note**: if the react platform is used as a concrete implementation of the [*ChatPlatform*](https://github.com/xatkit-bot-platform/xatkit-chat-platform) the following property must be set in the Xatkit configuration:

```properties
xatkit.platforms.abstract.ChatPlatform = com.xatkit.plugins.react.platform.ReactPlatform
```

Xatkit React Platform
=====

[![License Badge](https://img.shields.io/badge/license-EPL%202.0-brightgreen.svg)](https://opensource.org/licenses/EPL-2.0)
[![Wiki Badge](https://img.shields.io/badge/doc-wiki-blue)](https://github.com/xatkit-bot-platform/xatkit-releases/wiki/Xatkit-React-Platform)

Receive and send messages from our [React-based chat component](https://github.com/xatkit-bot-platform/xatkit-chat-widget).

The React platform is a concrete implementation of the [*ChatPlatform*](https://github.com/xatkit-bot-platform/xatkit-chat-platform).

## Providers

The React platform defines the following providers:

| Provider                   | Type  | Context Parameters | Description                                                  |
| -------------------------- | ----- | ------------------ | ------------------------------------------------------------ |
| ChatProvider | Intent | - `chat.channel`: the identifier of the channel that sent the message<br/> - `chat.username`: the name of the user that sent the message<br/> - `chat.rawMessage`: the raw message sent by the user (before NLP processing) | Receive messages from a communication channel and translate them into Xatkit-compatible intents (*inherited from [ChatPlatform](https://github.com/xatkit-bot-platform/xatkit-chat-platform)*) |
| ReactIntentProvider | Intent | - `react.channel`: the identifier of the react channel that sent the message<br/> - `react.username`: the name of the react user that sent the message<br/> - `react.rawMessage`: the raw message sent by the user (before NLP processing) | Receive messages from the react component and translates them into Xatkit-compatible intents. Note that `react.channel`, `react.username`, and `react.rawMessage` contain the same values as `chat.channel`, `chat.username`, and `chat.rawMessage` |
| ReactEventProvider | Event | - | Receive non-textual events from the react component and translates them into Xatkit-compatible events. |

### ReactEventProvider Events

| Event         | Context                                       | Parameters                                                   | Description                                                  |
| ------------- | --------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| Client_Ready  | - `react`<br/>- `react_ready` (*lifespan 2*)  | - `react.channel` (**String**): the identifier of the react channel associated to the new client<br/>- `react.hostname` (**String**): the  hostname of the page where the bot is accessed<br/>- `react.url` (**String**): the url of the page where the bot is accessed<br/>- `react.origin` (**String**): the origin of the page where the bot is accessed. | Event sent when a new react client connects to the Xatkit. **Note**: this event sets the context parameter `react.channel`, allowing to use a `Reply` action to post a message in response. |
| Client_Closed | - `react`<br/>- `react_closed` (*lifespan 2*) | - `react.channel` (**String**): the identifier of the react channel associated to the closed client | Event sent when a react client disconnects from Xatkit. **Note**: this event sets the context parameter `react.channel`, but **does not ensure that a `Reply` action invocation as a response to this event will be successful**. |

## Actions

| Action | Parameters                                                   | Return                         | Return Type | Description                                                 |
| ------ | ------------------------------------------------------------ | ------------------------------ | ----------- | ----------------------------------------------------------- |
| PostMessage | - `message`(**String**): the message to post<br/> - `buttons` (**List[String]**, *Optional*): a list of values to render as quick message buttons<br/> - `channel` (**String**): the identfier of the react channel to post the message to | The posted message | String | Posts the provided `message` to the given react `channel` (*inherited from [ChatPlatform](https://github.com/xatkit-bot-platform/xatkit-chat-platform)*). If the `buttons` parameter is specified the chat window will also print quick message buttons to drive the conversation. |
| Reply | - `message` (**String**): the message to post as a reply <br/> - `buttons` (**List[String]**, *Optional*): a list of values to render as quick message buttons | The posted message | String | Posts the provided `message` as a reply to a received message (*inherited from [ChatPlatform](https://github.com/xatkit-bot-platform/xatkit-chat-platform)*). If the `buttons` parameter is specified the chat window will also print quick message buttons to drive the conversation. |
| ReplyLinkSnippet | - `title` (**String**): the title of the snippet <br/> - `link` (**String**): the link of the snippet <br/> - `img` (**String**): the URL of the image to display | `null` | `null` | Creates a link snippet with a preview image. |
| ToggleDarkMode | - | `null` | `null` | Notifies the client to enable/disable dark mode |
| Wait | - `delay` (**Integer**): the delay to wait | `null` | `null` | Pauses the execution for the given `delay`. |

## Markdown Formatting

The [Xatkit React Widget](https://github.com/xatkit-bot-platform/xatkit-chat-widget) relies on [markdown-it](https://github.com/markdown-it/markdown-it) to parse and render markdown. The class `com.xatkit.plugins.react.platform.utils.MessageUtils` provides some utility methods to render markdown elements from your bot code:

```java
// Renders a new line
MessageUtils.NEW_LINE;
// Renders a bullet list with the elements of the provided list
MessageUtils.itemizeList(Arrays.asList("abc", "def"));
// Renders an enumeration with the elements of the provided list
MessageUtils.enumerateList(Arrays.asList("abc", "def"));
```



The React widget can also render *eventLinks*, which are `<a>` tags that do not redirect to a real resource but are interpreted as user input. This allows to design rich conversation patterns where users can click on the relevant elements presented by the bot. You can use the following helper to render *eventLinks*:

```java
/*
 * Renders a "name" link that is interpreted as "value".
 * This is typically used for localization: the displayed name can be translated while the internal value remains 
 * in your development language.
 */
MessageUtils.eventLink("name", "value");
/*
 * Syntactic sugar for eventLink("name", "name").
 * Use this method if you don't need to differenciate the displayed name from the value.
 */
```

## Options

The React platform supports the following configuration options

| Key                                | Values  | Description                                                  | Constraint                                                   |
| ---------------------------------- | ------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `xatkit.react.client.url`          | String  | The base URL of the website displaying the react chat component. Supports wildcard (`"*"`) to allow any domain. | **Optional** (default to `<xatkit.server.public_url>:<xatkit.server.port>`) |
| `xatkit.react.port`                | Integer | The port used to start the socket server used by the React platform. | **Optional** (default `5001`)                                |
| `xatkit.react.public_url`          | String  | The public URL of the socket.io server. This property is used in the generated HTML page accessible at `/admin`, and allows to specify a custom location for the react server. | **Optional** (default `http://localhost:5001`)               |
| `xatkit.react.enable_testing_page` | Boolean | Enables/disables the testing page located at `/admin`.       | **Optional** (default `true`)                                |

**Note**: if the react platform is used as a concrete implementation of the [*ChatPlatform*](https://github.com/xatkit-bot-platform/xatkit-chat-platform) the following property must be set in the Xatkit configuration:

```properties
xatkit.platforms.abstract.ChatPlatform = com.xatkit.plugins.react.platform.ReactPlatform
```

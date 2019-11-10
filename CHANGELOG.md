# Changelog

All notable changes for the React platform will be documented in this file.

Note that there is no changelog available for the initial release of the platform (2.0.0), you can find the release notes [here](https://github.com/xatkit-bot-platform/xatkit-react-platform/releases).

The changelog format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/v2.0.0.html)

## Unreleased

### Added

- Configuration property `xatkit.react.client.url` to specify the URL of client domains embedding the react chat component.  This property is translated into a `Access-Control-Allow-Origin` header that tells the browser to authorize requests from the specified domain. Supported values for the property are single URL and wildcard (`"*"`). The standard does not authorize multiple URLs in the `Access-Control-Allow-Origin` header (see [here]( https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Origin )).

## [3.0.0]- 2019-10-10

### Added

- Actions `ReplyFileMessage(file)` and `ReplyFileMessage(file, message)` to post messages containing file through the react component. These actions upload the provided file on Xatkit's public directory (see [this commit on xatkit-runtime](https://github.com/xatkit-bot-platform/xatkit-runtime/commit/cdb0521320e2606a4ae3a5e4c12618ad018afaf8)). If the file is an image (`.gif`, `.jpg`, or `.png`) the react component prints a clickable thumbnail of the image, otherwise a link is created to open the file or download it.

### Changed

- `ReactIntentProvider` and `ChatProvider` now use the new intent provider hierarchy (see [xatkit-runtime/#221](https://github.com/xatkit-bot-platform/xatkit-runtime/issues/221)).
- React platform's intent providers and actions now use [socket.io](https://socket.io/) to receive messages and reply in real-time. **This change breaks the public API**: the REST endpoint `/react/getAnswers` doesn't exist anymore, and messages must be passed as JSON objects through the socket).

## [2.0.0] - 2019-08-20 

See the release notes [here](https://github.com/xatkit-bot-platform/xatkit-react-platform/releases).

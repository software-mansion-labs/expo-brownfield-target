# APIs

### Table of Contents

- [Communication](#communication)

  - [Overview](#communication-overview)

  - [React Native API](#communication-api-js)

  - [Android API](#communication-api-android)

  - [iOS API](#communication-api-ios)

- [Navigation](#navigation)

  - [Overview](#navigation-overview)

  - [React Native API](#navigation-api-js)

<a href="communication"></a>
## Communication

<a href="communication-overview"></a>
### Overview

Communication API enables bi-directional, message-based communication between the native (host) app and the brownfield app. Messages are defined as:

- `Record<string, any>` in JavaScript

- `[String: Any?]` in Swift

- `Map<String, Any?>` in Kotlin

<a href="communication-api-js"></a>
### React Native API 

`addListener`

Registers a listener for the `onMessage` event

```ts
ExpoBrownfieldModule.addListener(listener: (event: MessageEvent) => void): EventSubscription
```

Arguments:

| Name | Required | Description | Platform support | Default value |
| --- | --- | --- | --- | --- |
| `listener` | Yes | `onMessage` event listener | All | - |

Example:

```tsx
import ExpoBrownfieldModule, { type MessageEvent } from 'expo-brownfield-target';

// ...

const handleEvent = (event: MessageEvent) => {
    setLastEvent(event);
    updateEventCount();
};

useEffect(() => {
    ExpoBrownfield.addListener(handleEvent);

// ...
```

----

<br />

`listenerCount`

Returns the count of listeners registered for the `onMessage` event

```ts
listenerCount(): number
```

Example:

```tsx
import ExpoBrownfieldModule from 'expo-brownfield-target';

// ...

const activeListeners = ExpoBrownfieldModule.listenerCount();
```

----

<br />

`removeAllListeners`

Removes all listeners registered for the `onMessage` event.

```ts
removeAllListeners(): void
```

Example:

```tsx
import ExpoBrownfieldModule from 'expo-brownfield-target';

// ...

useEffect(() => {
  return () => {
    ExpoBrownfieldModule.removeAllListeners();
  };
});
```

----

<br />

`removeListener`

Removes the specified `onMessage` event listener

```ts
removeListener(listener: (event: MessageEvent) => void): EventSubscription
```

Arguments:

| Name | Required | Description | Platform support | Default value |
| --- | --- | --- | --- | --- |
| `listener` | Yes | `onMessage` event listener | All | - |

Example:

```tsx
import ExpoBrownfieldModule, { MessageEvent } from 'expo-brownfield-target';

// ...

const handleEvent = (event: MessageEvent) => {
    setLastEvent(event);
    updateEventCount();
};

useEffect(() => {
    ExpoBrownfield.addListener(handleEvent);
    return () => {
        ExpoBrownfield.removeListener(handleEvent);
    }
});
// ...
```

----

<br />

`sendMessage`

Sends a message from JavaScript to the native apps

```ts
sendMessage(message: Record<string, any>): void
```

Arguments:

| Name | Required | Description | Platform support | Default value |
| --- | --- | --- | --- | --- |
| `message` | Yes | Message payload | All | - |

Example:

```tsx
import ExpoBrownfieldModule from 'expo-brownfield-target';

// ...

ExpoBrownfieldModule.sendMessage({
  type: "MyMesage",
  data: {
    language: "TypeScript",
    expo: true,
    nativePlatforms: 2,
    platforms: ["android", "ios"],
  },
});
```

<a href="communication-api-android"></a>
### Android API

`addListener`

Registers a listener for messages from JavaScript. Returns the UUID of the listener which can be used to remove it

```kotlin
addListener(callback: (Map<String, Any?>) -> Unit)
```

Arguments:

| Name | Required | Description | Type | Default value |
| --- | --- | --- | --- | --- |
| `callback` | Yes | Callback invoked with the incoming message | (Map<String, Any?>) -> Unit | - |

Example:

```kotlin
import expo.modules.brownfield.BrownfieldMessaging

// ...

val listenerId = BrownfieldMessaging.addListener { event ->
    println("Message listener: $event")
}
```

----

<br />

`removeListener`

Removes listener with the specified UUID

```kotlin
removeListener(id: String)
```

Arguments:

| Name | Required | Description | Type | Default value |
| --- | --- | --- | --- | --- |
| `id` | Yes | UUID of the listener to be de-registered | String | - |

Example:

```kotlin
import expo.modules.brownfield.BrownfieldMessaging

// ...

BrownfieldMessaging.removeListener(listenerId)
```

----

<br />

`sendMessage`

Emits a `onMessage` event with the message from Android to the JavaScript listeners

```kotlin
sendMessage(message: Map<String, Any?>)
```

Arguments:

| Name | Required | Description | Type | Default value |
| --- | --- | --- | --- | --- |
| `message` | Yes | Message payload | Map<String, Any?> | - |

Example:

```kotlin
import expo.modules.brownfield.BrownfieldMessaging

// ...

BrownfieldMessaging.sendMessage(mapOf(
    "type" to "MyAndroidMessage",
    "timestamp" to System.currentTimeMillis(),
    "nestedObject" to mapOf(
        "platform" to "android",
        "number" to 123.456,
        "chunks" to listOf("Hello", "from", "Android")
    )
))
```

<a href="communication-api-ios"></a>
### iOS API

`addListener`

Registers a listener for messages from JavaScript. Returns the UUID of the listener which can be used to remove it

```swift
addListener(_ callback: @escaping ([String: Any?]) -> Void)
```

Arguments:

| Name | Required | Description | Type | Default value |
| --- | --- | --- | --- | --- |
| `callback` | Yes | Callback invoked with the incoming message | @escaping ([String: Any?]) -> Void | - |

Example:

```swift
let listenerId = BrownfieldMessaging.addListener { event ->
    print("Message listener: \(event)")
}
```

----

<br />

`removeListener`

Removes listener with specified UUID

```swift
removeListener(id: String)
```

Arguments:

| Name | Required | Description | Type | Default value |
| --- | --- | --- | --- | --- |
| `id` | Yes | UUID of the listener to be de-registered | String | - |

Example:

```swift
BrownfieldMessaging.removeListener(id: listenerId)
```

----

`sendMessage`

Emits an `onMessage` event with the message from iOS to the JavaScript listeners

```swift
sendMessage(_ message: [String: Any?])
```

Arguments:

| Name | Required | Description | Type | Default value |
| --- | --- | --- | --- | --- |
| `message` | Yes | Message payload| [String: Any?] | - |

Example:

```swift
BrownfieldMessaging.sendMessage([
  "type": "MyIOSMessage",
  "timestamp": Date().timeIntervalSince1970,
  "nestedObject": [
    "platform": "ios",
    "number": 123.456,
    "chunks": ["Hello", "from", "iOS"]
  ]
])
```

<a href="navigation"></a>
## Navigation

<a href="navigation-overview"></a>
### Overview

Navigation API provides two JavaScript methods for implementing common brownfield patterns like returning to the native (host) app (regardless of React Native navigation state) and controlling whether back button/back gesture should be handled by host or React Native app.

<a href="communication-api-js"></a>
### React Native API

`popToNative`

A method to return to the native view which precedes the React Native brownfield in the navigation history

```ts
popToNative(animated?: boolean)
```

Arguments:

| Name | Required | Description | Platform support | Default value |
| --- | --- | --- | --- | --- |
| `animated` | No | Specifies if the return to the native view should be performed with an animation | iOS (UIKit) | `false` |

Example:

```tsx
import ExpoBrownfieldModule from 'expo-brownfield-target';

// ...

<Button 
  title="Go back" 
  onPress={() => ExpoBrownfieldModule.popToNative()} 
/>

<Button 
  title="Go back animated" 
  onPress={() => ExpoBrownfieldModule.popToNative(true)} 
/>

```

----

<br />

`setNativeBackEnabled`

Enables or disables native handling of the back action (the back gesture on iOS or back button on Android).

```ts
setNativeBackEnabled(enabled: boolean)
```

Arguments:

| Name | Required | Description | Platform support | Default value |
| --- | --- | --- | --- | --- |
| `enabled` | Yes | If native handling of the back action should be enabled or disabled | All | - |

Example:

```tsx
import ExpoBrownfieldModule from 'expo-brownfield-target';
import { useNavigation } from "expo-router";

// ...

export default function HomeScreen() {
  const navigation = useNavigation();

  useEffect(() => {
    const unsubscribe = navigation.addListener("state", () => {
      // Enable native handling only when we can't further go back
      // within the React Native app
      const shouldEnableNativeBack = navigation.canGoBack();
      ExpoBrownfield.setNativeBackEnabled(!shouldEnableNativeBack);
    });

    return () => {
      unsubscribe();
    };
  }, [navigation]);

  // ...
```

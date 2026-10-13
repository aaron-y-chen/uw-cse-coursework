# Problem Set 1: RPC semantics

## Background

A challenge for distributed systems is that neither computers nor networks are perfect. Computers can crash or be slow, and networks can drop, duplicate, or reorder messages, or be slow.

It might seem that TCP addresses many of these issues, since it is designed to retransmit missing packets, but TCP itself can fail. If a client is in the middle of sending some operation to a server and the WiFi router crashes, the TCP connection may reset after 30 seconds or so. When the WiFi reconnects, should the client resend an operation that might have been in progress? If it doesn't, maybe the operation never reached the server. If it does, what if the operation previously got through and completed? Doesn't that risk doing it twice?

This problem set is about coordinating the client and server so that the intended behavior occurs. Assume the network can delay, drop, reorder, or duplicate messages, but does not corrupt them, and that computers are fail-stop: if they don't crash, they follow the protocol.

Consider the variants of RPC from lecture:

* **Naive RPC**. Send each RPC request and reply as a message, with no identifier and no retransmission.
* **Naive RPC with request IDs**. Naive RPC plus per-client sequence numbers on requests and responses, so that different requests and responses can be told apart.
* **At least once RPC**. Naive RPC with request IDs, plus the client retransmits its request until it gets a response (or gives up).
* **At most once RPC**. The server tracks which requests it has already executed, and retransmits the response it previously gave if it receives the same request again.
* **Exactly once RPC**. At most once RPC, plus the client retransmits its request until it gets a response (or gives up).

```note
Definition: executed

The server executes a request when it runs the operation carried by the request.
```

## 1. Naive RPC with request IDs

These questions assume the "Naive RPC with request IDs" protocol, with no retransmission. No explanation is required for this part.

1. Suppose a client sends a request and doesn't get a response. Is it possible that the request wasn't executed by the server?
2. Suppose a client sends a request and doesn't get a response. Is it possible that the request was executed by the server?
3. Is it possible for a client to send a request and get a response to that request from the server, but that request wasn't actually executed on the server?

## 2. At least once and at most once

Answer these yes/no questions with explanation: if your answer is yes, briefly describe an execution (a sequence of events) where the scenario occurs; if your answer is no, explain why not in one sentence.

1. At least once. In the "At least once RPC" protocol, is it possible for a client to send a request and get a response to that request from the server, but that request was executed on the server more than once?
2. At most once. In the "At most once RPC" protocol, is it possible for a client to send a request and get a response to that request from the server, but that request was executed on the server more than once?
3. One RPC at a time. In the labs, each client waits until it gets a successful response to its previous request (A) before sending the next request (B). Under at least once RPC, suppose the server receives B. Can the server later receive request A?

## 3. Multiple RPCs in flight

Now suppose we relax the requirement that the client sends one RPC at a time. (This question is just to deepen your understanding of RPCs. In the labs, we will require the client to send one RPC at a time.) Suppose the client sends requests a, b, c, d, and then e, in that order, without waiting for the reply to a (or b, c, or d). The client has a retransmit timer, each RPC is labelled with a unique ID, the protocol is "at least once RPC", and the network may drop, duplicate, reorder, or delay individual messages. Each question is yes or no.

1. Is it possible for the server to receive the requests in this order: a, b, d, e, c?
2. Is it possible for the server to receive the requests in this order: a, b, c, d, e, c?
3. Assuming the client keeps retransmitting each request until it gets a response for it, is any sequence of request arrivals at the server that includes all five messages impossible?

Name      : Aaron Chen  
Student ID: aaronyc

## 1. Naive RPC with request IDs

These questions assume the "Naive RPC with request IDs" protocol, with no retransmission. No explanation is required for this part.

1. Suppose a client sends a request and doesn't get a response. Is it possible that the request wasn't executed by the server?

Yes, it's possible. If the request gets lost during transmission, the server won't execute it.

2. Suppose a client sends a request and doesn't get a response. Is it possible that the request was executed by the server?

Yes, it's possible. The server may have executed the request, but the response was lost during transmission.

3. Is it possible for a client to send a request and get a response to that request from the server, but that request wasn't actually executed on the server?

If a client sends a request to the server, but the server encounters an error and responds with an error message containing the same id, the server may not have executed the request. Therefore, I think it's possible.

## 2. At least once and at most once

Answer these yes/no questions with explanation: if your answer is yes, briefly describe an execution (a sequence of events) where the scenario occurs; if your answer is no, explain why not in one sentence.

1. At least once. In the "At least once RPC" protocol, is it possible for a client to send a request and get a response to that request from the server, but that request was executed on the server more than once?

Yes, if the client sends a request every 60 seconds, but the server takes more than 60 seconds to execute it, the server may receive and execute another request before sending a response to the first one.

2. At most once. In the "At most once RPC" protocol, is it possible for a client to send a request and get a response to that request from the server, but that request was executed on the server more than once?

No, it's impossible. With the same id, the server won't executed the request more than once.

3. One RPC at a time. In the labs, each client waits until it gets a successful response to its previous request (A) before sending the next request (B). Under at least once RPC, suppose the server receives B. Can the server later receive request A?

Yes, it's possible. For example:

```text
1. The client sends request A.
2. The client waits for N seconds and then retransmits request A because it uses at-least-once RPC.
3. The client receives response A after N+1 seconds and sends request B.
4. Due to a network delay, the retransmitted request A arrives at the server after request B. Therefore, the server may execute B before executing A again.
```

## 3. Multiple RPCs in flight

Now suppose we relax the requirement that the client sends one RPC at a time. (This question is just to deepen your understanding of RPCs. In the labs, we will require the client to send one RPC at a time.) Suppose the client sends requests a, b, c, d, and then e, in that order, without waiting for the reply to a (or b, c, or d). The client has a retransmit timer, each RPC is labelled with a unique ID, the protocol is "at least once RPC", and the network may drop, duplicate, reorder, or delay individual messages. Each question is yes or no.

1. Is it possible for the server to receive the requests in this order: a, b, d, e, c?

It's possible. For example:

```text
1. The client sends request a, and the server receives it.
2. The client sends request b, and the server receives it.
3. The client sends request c, but it is delayed by the network.
4. The client sends request d, and the server receives it.
5. The client sends request e, and the server receives it.
6. The server finally receives request c.
```

2. Is it possible for the server to receive the requests in this order: a, b, c, d, e, c?

I think it's possible. If the response to request c is lost while the other responses arrive successfully, the client will retransmit request c. Therefore, the server could receive the requests in this order: a, b, c, d, e, c.

3. Assuming the client keeps retransmitting each request until it gets a response for it, is any sequence of request arrivals at the server that includes all five messages impossible?

It's possible. Consider the following scenario:

```text
0s:  The client sends a, b, c, d, e.
Ns:  No responses have arrived, so the client retransmits a, b, c, d, e.
2Ns: No responses have arrived, so the client retransmits a, b, c, d, e.
3Ns: No responses have arrived, so the client retransmits a, b, c, d, e.
...
```

Because the network can delay and reorder messages, the server can receive the requests in any order.

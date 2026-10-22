Name      : Aaron Chen  
Student ID: aaronyc

# The View Server Ping protocol

### 1.a. Describe an infinite execution of the View Server Ping protocol where a server crashes but the view server considers it alive forever. Be sure your execution satisfies timer fairness.

Let's assume there's only one server called A and one view server. The following scneario could led the view server considers server A alive forever.

1. A sends a ping to the view server
2. A crashes
3. The network keeps duplicating and delivering that old ping forever
4. The view server will considers A alive forever

### 1.b. Describe an infinite execution of the View Server Ping protocol where a server does not crash, but the view server considers it down forever. Be sure your execution satisfies timer fairness.

If the network drops every ping from server A, then the view server continues to consider server A dead forever.

# Primary-backup

### 2. Describe at a high level a finite execution of this system with only one client that submits only `Append` operations on the key `foo`, and only submits one request at a time, where a later append returns a result that contradicts an earlier append to the same key. (Hint: Use a view change.)

Let's assume we have the view server and three servers A, B, and C. A is primary and B is backup, and B will be primary and C will be backup if A crashes.

1. The client sends `Append("foo", "a")`
2. A receives it and forwards the request to B, and both execute it
3. A crashes, B becomes primary but without state transfer to C
4. B crashes, C becomes primary
5. The client sends `Append("foo", "b")`
6. The client sends `Get("foo")`, and it will receive `"b"`

### 3.a.1. Who is the primary of the next view? Who is the backup?

S2 will be the primary, and S3 will be the backup.

### 3.a.2. After state transfer completes in the new view, has 𝑟 been executed on the new primary's application or not?

`r` has been executed on the new primary's application.

### 3.a.3 If you said 𝑟 has been executed: has 𝐶1 received a response yet? If yes, say when it was sent. If no, say what 𝐶1 can do to get one. If you said 𝑟 has not been executed: how can 𝐶1 get it executed?

`C1` has not received a response, and `C1` will re-send a request to primary again. This time, primary (S2) will respond to `C1` properly.

### 3.b.1. Who is the primary of the next view? Who is the backup?

S1 will be the primary and S3 will be the backup.

### 3.b.2. After state transfer completes in the new view, has 𝑟 been executed on the new primary's application or not?

No, `r` has not been executed on the new primary's application.

### 3.b.3. If you said 𝑟 has been executed: has 𝐶1 received a response yet? If yes, say when it was sent. If no, say what 𝐶1 can do to get one. If you said 𝑟 has not been executed: how can 𝐶1 get it executed?

C1 can retry and get a response.

### 3.c. Using the two previous parts, can 𝐶1 tell immediately whether or not its request has been executed? If yes, describe how. If not, explain why not, and how 𝐶1 can still make progress without knowing which scenario occurred.

It seems that C1 can't immediately tell whether its request has been executed because we can't guarantee that the servers won't crash. C1 can periodically resend the request until it receives a response.

### 4.a. The view server may pick any idle server as the primary of view 𝑖 +1, even one that was neither the primary nor the backup of view 𝑖. The new primary still does state transfer to the new backup.

For 4.a, I think the following scenario would lead to an incorrect result:

1. S1 is the primary, and S2 is the backup
2. The client sends Append("foo", "a")
3. After S1 and S2 execute it, S1 crashes
4. S3 becomes the new primary
5. The client sends Append("foo", "b")
6. The client calls Get("foo") and receives "b"

### 4.b. The view server may change the view even if the previous view has not been acknowledged by its primary.

I think there might be two primaries at the same time in this scenario.

### 4.c. When the primary receives a client request, it first executes the request locally and immediately replies to the client, and only then forwards the request to the backup and waits for the backup's acknowledgment before executing any further requests.

The data might be lost if the following happens:

1. S1 is primary, and S2 is backup
2. S1 receives Append("foo", "a")
3. S1 executes it and responds to the client before S2 acknowledges it
4. S1 crashes before S2 executes the request
5. S2 becomes primary without "a"
6. The client sends Append("foo", "b") and gets "b" instead of "ab"

### 5. Explain briefly (a few sentences) why it is not a bug that the old view can keep executing requests after the view server has moved on to the new view. In particular, explain why the new view and the old view cannot operate independently and diverge from each other.

For the first part: I think it's because once the request is successfully forwarded from the old primary to the new primary, both servers have the same result.

For the second part: if S1 wants to execute a request, it must wait for an ACK from S2. Once S2 becomes the primary, it will stop sending ACKs to S1, so S1 won't execute any more requests.

# Invariant, stable, or unstable

### 6.a. "Server 𝑆1 is the primary in view 3."

Stable. View 3 has not been created at first, so the statement is not true. Once S1 becomes primary in view 3, this statement will never change.

### 6.b. "Server 𝑆1 is the primary of the current view."

I think it's unstable because once S1 crashes, another server, such as S2, will become the primary of the current view.

### 6.c. "If the current view number is greater than 0, then the current view has a primary."

Invariant. Even if no servers are alive, the current view still has a server assigned as the primary.

### 6.d. "If the current view number is greater than 0, then the current view has a backup."

Unstable. If there's no idel server, then backup will be assigned to null.

### 6.e. "If the current view number is greater than 0, then the primary of the current view has not crashed."

I think it's unstable. For example, S1 could be the primary and be alive, making the statement true. But if S1 crashes later and no new view is created, the statement becomes false.

### 7.a. "If a response message with sequence number 𝑛 has been sent to client 𝑐, then the corresponding request has been executed on the current view's primary's application."

Invariant.

1. When the primary sends a response to the client, it means the backup has already executed the request
2. If the primary crashes, the backup becomes the new primary. Therefore, the current primary has also executed the request

### 7.b. "If 𝑆1 is the primary of view 𝑖 and 𝑆2 is the backup of view 𝑖, and 𝑆2 has sent 𝑆1 a forwarded-request acknowledgment with view number 𝑖 for request 𝑟, then 𝑟 has been executed on 𝑆2's application."

Unstable.

1. S1 is primary, and S2 is backup
2. S2 executes r and sends an ACK, but the ACK is lost
3. The view server mistakenly considers S2 dead because its pings are lost
4. S3 replaces S2 as backup. S2 is still alive
5. Later, S2 rejoins as backup and receives state transfer from S1
6. Since S1 never executed r, the state transfer overwrites S2's state, removing r

### 7.c. "If 𝑆1 is the primary of view 𝑖 and 𝑆2 is the backup of view 𝑖, and 𝑆2 has sent 𝑆1 a forwarded-request acknowledgment with view number 𝑖 for request 𝑟, then 𝑟 has been executed on the current view's backup's application."

Unstable. I think we can reuse the example I mentioned in 7.b.

### 7.d. (Recall that the primary-backup protocol replicates the entire AMOApplication, not just the key-value store.) "If 𝑆1 is the primary of view 𝑖 and 𝑆2 is the backup of view 𝑖, and 𝑆2 has sent 𝑆1 a forwarded-request acknowledgment with view number 𝑖 for request 𝑟, then 𝑆2's AMOApplication has an entry for 𝑟's client with a sequence number at least as large as 𝑟's."

Unstable. The same example as in 7.b. applies. When S1 transfers its state to S2, the sequence number could be overwritten with a value smaller than r's sequence number.

# Linearizability

### 8.a.

Linearizable. Append(k, x) -> Get(k)

### 8.b.

Linearizable. Get(k) -> Append(k, x)

### 8.c.

Not Linearizable. If it were lineariable, then Append(k, x) -> Get(k) should not return KeyNotFound()

### 8.d.

Not Linearizable. If it's linearizable, C2 must receive KeyNotFound() because C2 receives a response before C1 sends Append(k, x). However, the question says C2 receives GetResult(x), which means it's not linearizable.

### 8.e.

Linearizable. Append(k, y) -> Append(k, x)

### 8.f.

Not linearizable. C2 receives AppendResult(y), indicating that Append(k, y) was executed before Append(k, x). However, C1 receives AppendResult(xy), which suggests an inconsistent execution order.

### 8.g.

Not linearizable. C1 receives AppendResult(yx) that means Append(k, y) -> Append(k, x). C2 receives AppendResults(xy) that means Append(k, x) -> Append(k, y). These two results contradict each other, so it's not linearizable.

### 8.h.

Lineariable. Get(k) -> Append(k, x) -> Get(k)

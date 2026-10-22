Name      : Aaron Chen  
Student ID: aaronyc

# View server

### What does the view server need to keep track of about each server?

The view server needs to keep track of each server's liveness. Once it detects that the primary server is down, it needs to update the configuration, including the primary server, backup server, and view number. The view server must also track whether the current primary has acknowledged the current view and detect whether any server has restarted.

# Clients

### How does a client learn the current primary, and when does it ask the view server again?

The client learns the current primary by asking the view server. If the primary doesn't respond after a timeout and several retries, the client asks the view server again. If the view server reports the same primary, the client repeats the timeout and retry process.

# Replication

### How does the primary verify that a request has successfully been replicated to the backup?

The primary needs to wait for a response from B to make sure B has executed the request.

Name      : Aaron Chen  
Student ID: aaronyc

1. What is the difference between a ballot and a slot?

Ballot: Servers use ballots to elect a leader and reject proposals from older rounds.

Slot: One position in the command log. The slot number determines the command's execution order.

2. Once elected, what does the new leader do with the slots it learned about? Why must it re-propose values that were accepted but not known to be chosen?

The new leader keeps values known to be chosen. For each other slot with accepted values, it selects the value with the highest accepted ballot from its Phase 1 majority replies and propose it again.

An accepted value might already have been chosen, even if the new leader does not know this. This rule prevents a different value from being chosen for the same slot.

3. What happens to commands that were in flight when the leader died?

The other server will start an election with a higher ballot. The client will retry their command due to no result, and the new leader will propose the command for a slot.

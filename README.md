# LocInfer
This is part of implementations of our CNS 2015 paper "Your Actions Tell Where You Are: Uncovering Twitter Users in a Metropolitan Area": http://arxiv.org/abs/1503.08771.

LocInfer aims to find the location for a Twitter user based on his/her neighbors.
Given a set of seed users in an geographical area, a set of candidate user, and the network connection between then, 
FoundUsers.java finds the users from the candidate set who are in the same area with high probability.

We use zip compression and min-heap to reduce the memory requirement and computation time.

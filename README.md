# LocInfer
This is part of implementations of our IEEE CNS 2015 paper "Your Actions Tell Where You Are: Uncovering Twitter Users in a Metropolitan Area": http://arxiv.org/abs/1503.08771.

Existing work focus on inferring the location for a Twitter user based on his/her content and neighbors. However, LocInfer aims to find the majority of the Twitter users in any metropolitan area without checking all the users in the Twitter.

Given a set of seed users in an geographical area, a set of candidate user, and the network connection between then, 
FoundUsers.java finds the users from the candidate set who are in the same area with high probability.

We use zip compression and min-heap to reduce the memory requirement and computation time.

# KREB-J
JAVA repository linked to KREB project.

Standalone process which watches for Kafka's Topic's new messages coming from Redmine. 
Automates and batches email sending to its original recipients. 
This tool is born due to Office365 policies; stablishing a workaround solution for its max-emails-sent-per-minute rate.

This repository includes the JAVA standalone app which consumes messages from a Kafka Topic, to eventually send them as emails through Office365 to its recipients while avoiding email loss; due to current max-emails-sent-per-minute rate policies.

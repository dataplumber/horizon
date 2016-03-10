# Horizon: The horizontal-scale data ingestion and management framework

The Horizon framework provides several core components and services that are common to a distributed archive center, including data crawlers, Ingest Client/Server, Inventory API, Archive, Distribution, Operator Tool, ManagerWS, SigEventWS, and SecurityWS. The goal is to create a framework which projects only require implementing domain-specific components like Data Handlers and Inventory to have a running, horizontally scaled data system. 

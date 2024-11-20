# Map-Reduce Server System for booking Application
This repository provides a **MapReduce** server system designed to handle booking data efficiently for large-scale applications. 
Leveraging the MapReduce paradigm, the system distributes and processes vast amounts of booking information across multiple nodes, ensuring high scalability and fault tolerance. 
The *map* phase processes and filters booking requests, while the *reduce* phase aggregates results.

# Setup
1. Run the [Master.java](src/main/java/org/example/Master.java)
2. Run the [Worker.java](src/main/java/org/example/Worker.java) as many times as the worker you want
3. Run the [Reducer.java](src/main/java/org/example/Reducer.java)
4. Run the [Tenant.java](src/main/java/org/example/Tenant.java) or the [ManagerApp](src/main/java/org/example/ManagerApp.java)
   

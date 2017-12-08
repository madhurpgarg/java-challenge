# java-challenge

If I would have been given more time then,

1.  I could add more tests
2.  In order to deal with concurrency, would have tried to implement in functional way
3.  Would also like to refactor transfer method in AccountService using declarative style which is currently done using     
    imperative style

In order make this microservice production, we need fill some gaps which are still there.

1.  Implement the real notification service
2.  Use some containers tech like Docker to have this same service environment to production. Which will be configure to build the service, package it and deploy it any where.
3.  Database is still in memory of this service, so I assume we would be moving the DB to some real DB for handling               transactions
4.  It would be good if we can build a CI/CD pipeline to have deployment automated.
5.  Ofcourse, some more testing is needed in different enviroment with some real data.

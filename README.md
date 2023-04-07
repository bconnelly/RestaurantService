# RestaurantService

A simple API made up of this microservice as well as [OrdersService](https://github.com/bconnelly/OrdersService), [CustomersService](https://github.com/bconnelly/CustomersService) and [TablesService](https://github.com/bconnelly/TablesService). Hosted in a kubernetes cluster on AWS, records stored in a MySQL RDS instance. Commits to master are only made by Jenkins script if rc branch deploys successfully.

### Scripts
- __Jenkinsfile__: script for building, testing and deploying via a jenkins server
- __Dockerfile__: used during jenkins build after maven project successfully builds and passes tests
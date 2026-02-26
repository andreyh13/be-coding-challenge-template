Provided solution modifies NotifictionType.kt adding in the enum groups of the types 
and providing a method that returns group (set of types) for a given notification type.

In NotificationService filter was changed to check if exist intersection between types
user is subscribed to and a group of types the notification type is belonged to.

Corresponding tests implemented in CodeChallengeApplicationTests.
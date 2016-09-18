# Database Layout

## Users

### Primary Key
- ID (integer)
    - faster?
- Unique username (ensures no overlap/spoofing as should conflict, primary key assures this?)
- Both?

### User data

+ ID
+ Username
+ hashed password
+ salt
+ email address
+ Date created

## Posts

+ ID (unique to user)
+ Owner
+ Project Name
+ Post Title
+ Post content
+ Post Creation Date
+ Last Modified Date

### Primary Key
Concat of ID/Username/Project?

## Projects

+ ID
+ Owner
+ Title
+ Description
+ Date Created
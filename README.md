# Custom Cache Implementation

## Problem description 

An application needs a lightweight cache. For various reasons, reuse any of the readily available caching solutions is not an option. Hence, we need to create a home-grown solution. You have been tasked with creating this cache. Here are the acceptance criteria this cache solution.

## Must Haves: 
1. At the bare minimum the cache MUST be able to perform add(K key, V value) and get(Object) operations. 
2. The cache has its own type safety rules. \
    i. User can add key of any type and value as long as a key of that type is not already present. 
    ii. Once a unique key type is added any subsequent entries should allow values which are of the same type or its subclass/subtype. 
    Example: Let’s say we have a class hierarchy of Shape -> Rectangle -> Square The user should be able to add objects of all these classes to the cache against the same Key object say ShapeKey but if he tries to add a string or any object not from the Shape hierarchy against the ShapeKey it should fail. Once ShapeKey is removed from the cache then he should be able to add a String or any other object as value to ShapeKey. There are unit test covering this scenario (Eg: testSuperAndSubTypesTypes_RemoveAndAdd).
3. The caching solution should work in a single JVM multithreaded environment with no loss of functionality.

## Nice to Haves: 
  4. Avoid using any of the readily available java Map implementations to build the cache or to store caching data. Usage of List, Set, Arrays or any other Collection implementations is fine. 
  5. Solution allows for items to expire from cache at a preconfigured interval and this should be configurable at a key type level. Once a key type expires the rule of type safety should get reset.


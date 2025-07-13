
# Microservices with Resilience4j

## Circuit Breaker with Resilience4j
A circuit breaker is a design pattern used to handle failures gracefully in distributed systems. It prevents a service from making repeated requests to an unresponsive or slow service, thereby protecting the system from cascading failures.

### agar ye example na smjh aaye to niche author ,book wala example hai wo dekho aur feel lo,yha se bas ye pom.xml delh lo what dependency is required

## Example Scenario
We have three microservices:

### User Service:
Exposes an API to get user details and enriches it with ratings and hotel information.

### Rating Service:
Provides ratings data (rating, userId, hotelId).

### Hotel Service:
Provides hotel details based on hotelId.
The flow:

### User Service calls:
Rating Service to fetch ratings for a user.
Hotel Service to fetch hotel details for each rating.

How Circuit Breaker Works
If Rating Service or Hotel Service is down or slow:
The circuit breaker trips and prevents further calls for a certain period.
A fallback method is executed to return default or cached data.
The circuit breaker uses Resilience4j for implementation.


### Implementation

```
User Service
Controller Code
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserWithDetails(@PathVariable String userId) {
        User user = userService.getUserWithDetails(userId);
        return ResponseEntity.ok(user);
    }
}


```

```
user service code 
 
@Service
public class UserService {

    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "ratingService", fallbackMethod = "fallbackForRatings")
    public User getUserWithDetails(String userId) {
        // Fetch user details (mocked here)
        User user = new User(userId, "John Doe");

        // Call Rating Service
        Rating[] ratings = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/" + userId, Rating[].class);
        user.setRatings(Arrays.asList(ratings));

        // Fetch hotel details for each rating
        user.getRatings().forEach(rating -> {
            Hotel hotel = restTemplate.getForObject("http://HOTEL-SERVICE/hotels/" + rating.getHotelId(), Hotel.class);
            rating.setHotel(hotel);
        });

        return user;
    }

    // Fallback method
    public User fallbackForRatings(String userId, Throwable throwable) {
        User user = new User(userId, "John Doe");
        user.setRatings(Collections.emptyList()); // Return empty ratings
        return user;
    }
}



```


```
pom.xml

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>




```


```
application.properties

server.port=8081
spring.application.name=USER-SERVICE
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

# Resilience4j configuration
resilience4j.circuitbreaker.instances.ratingService.registerHealthIndicator=true
management.endpoints.web.exposure.include=health,metrics




```
```
Rating Service
Controller Code

@RestController
@RequestMapping("/ratings")
public class RatingController {

    @GetMapping("/users/{userId}")
    public List<Rating> getRatingsByUserId(@PathVariable String userId) {
        return List.of(
            new Rating(userId, "H1", 5),
            new Rating(userId, "H2", 4)
        );
    }
}


```
````
application.properties

server.port=8082
spring.application.name=RATING-SERVICE
eureka.client.service-url.defaultZone=http://localhost:8761/eureka


````


```
Hotel Service
Controller Code

@RestController
@RequestMapping("/hotels")
public class HotelController {

    @GetMapping("/{hotelId}")
    public Hotel getHotelById(@PathVariable String hotelId) {
        return new Hotel(hotelId, "Hotel " + hotelId, "City Center");
    }
}
application.properties

server.port=8083
spring.application.name=HOTEL-SERVICE
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

```


```
server.port=8080
spring.application.name=API-GATEWAY
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

spring.cloud.gateway.routes[0].id=USER-SERVICE
spring.cloud.gateway.routes[0].uri=lb://USER-SERVICE
spring.cloud.gateway.routes[0].predicates[0]=Path=/users/**

spring.cloud.gateway.routes[1].id=RATING-SERVICE
spring.cloud.gateway.routes[1].uri=lb://RATING-SERVICE
spring.cloud.gateway.routes[1].predicates[0]=Path=/ratings/**

spring.cloud.gateway.routes[2].id=HOTEL-SERVICE
spring.cloud.gateway.routes[2].uri=lb://HOTEL-SERVICE
spring.cloud.gateway.routes[2].predicates[0]=Path=/hotels/**



```

### How It Works
Frontend Calls API Gateway:

Request: http://localhost:8080/users/{userId}.
Gateway routes to User Service.
User Service Calls Other Services:

Calls Rating Service: http://RATING-SERVICE/ratings/users/{userId}.
Calls Hotel Service: http://HOTEL-SERVICE/hotels/{hotelId}.
Circuit Breaker:

If Rating Service or Hotel Service is down:
The circuit breaker trips and calls the fallback method.
Default data (e.g., empty ratings) is returned.
Actuator:

Circuit breaker health is exposed at: http://localhost:8081/actuator/health.
Benefits
Fault Tolerance: Circuit breaker prevents cascading failures.
Centralized Routing: API Gateway simplifies routing and service discovery.
Monitoring: Actuator provides insights into circuit breaker health and metrics.


## another example to get feel of circuit breaker and fault tolerance

### Fault tolerance refers to a system's ability to continue operating without interruption even when one or more of its components fail,

Microservices Example: Author and Book Service with Circuit Breaker and Fault Tolerance (Using Database and Postman Requests)
In this example, we have two microservices:

Author Service → Calls Book Service to fetch books written by an author.
Book Service → Stores and retrieves book details based on the author ID.
We will use:
✅ Feign Client for inter-service communication
✅ Circuit Breaker (Resilience4j) for fault tolerance

### Step 1: Setting Up Book Service
Book Entity

```
@Entity
public class Book {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String title;
private Long authorId;// for multiple books author can be same,many to one mapping,here author id will act 
//foreign key referring to author table

    
}


```
```
Book Repository

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByAuthorId(Long authorId);
}

```
Book Service
```


@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getBooksByAuthor(Long authorId) {
        return bookRepository.findByAuthorId(authorId);
    }
}

```
book contoller
```

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    public ResponseEntity<Book> saveBook(@RequestBody Book book) {
        return ResponseEntity.ok(bookService.saveBook(book));
    }

    @GetMapping("/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable Long authorId) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(authorId));
    }
}



```
```
server.port=8081
spring.application.name=BOOK-SERVICE
spring.datasource.url=jdbc:h2:mem:bookdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```


now

1)ek author table hai ek book table

2)ek author ke paas multiple books ho skta so author table contains list<Book> field

3)similarly Book entity mein ek author field hoga aur mutiple book ka author id same ho skta

4)so author service mein ek api aisa hona chayieh jaha id aaye author ka to pehle us id se author find ho jayega
aur kuki author mein ek field hai List<Book> so hm us author ka likha hua book bhi to chayieh but list of book to book service se
milega right so we need to call book service from author service (using feing client)

5)iske liye book service mein ek book repo mein ek method banana hoga List<Book> findByAuthorId(Long authorId)
aur isko call krenge using concept of feing

6)ek interface bnayennge author service mein wha url dal denge jo is book ke repo ko call krega

7)ab hume author details bhi mil gya aur book details bhi ab hum author.set(List<Book>) kar skte aur return kar skte

8)now here we can use concept of circuit breaker ,let say ki hume author mil gya but uska ek aur field jo list of books hai jo hm
book service call krke bula rhe to agar for some reason book service down hai to user ko kam se kam author ka to detail jaye na
books mein bhale empty jaye but humare case mein author ka detail bhi nhi jayega kuki us method mein run time jb book service call
hua aur wo service fail ho gya to server error aa jayega which is not user friendly

so we want ki agar service down hai to hum auhthor ka details dikha de aur books ke liye kuch custom error message dikha de
this we can do using circuit breaker

## implementation of circuit breaker

## Setting Up Author Service
### Feign Client to Call Book Service,, this will be made in author
```
@FeignClient(name = "BOOK-SERVICE", fallback = BookServiceFallback.class)
public interface BookClient {
    @GetMapping("/books/{authorId}")
    List<Book> getBooksByAuthor(@PathVariable Long authorId);
}
```
```
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    List<Book> books;
    // Constructors, Getters, and Setters
}
```
```
@Component
public class BookServiceFallback implements BookClient {
    @Override
    public List<Book> getBooksByAuthor(Long authorId) {
        return Collections.emptyList();
    }
}




```
```
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}

```
```
@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Optional<Author> getAuthor(Long id) {
        return authorRepository.findById(id);
    }
}

```
Author Controller
```
@RestController
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookClient bookClient;

    @PostMapping
    public ResponseEntity<Author> saveAuthor(@RequestBody Author author) {
        return ResponseEntity.ok(authorService.saveAuthor(author));
    }

    @GetMapping("/{authorId}/books")
    @CircuitBreaker(name = "bookService", fallbackMethod = "fallbackBooks")
    public ResponseEntity<Map<String, Object>> getAuthorWithBooks(@PathVariable Long authorId) {
        Optional<Author> author = authorService.getAuthor(authorId);
        if (author.isPresent()) {
            List<Book> books = bookClient.getBooksByAuthor(authorId);
            Map<String, Object> response = new HashMap<>();
            response.put("author", author.get());
            response.put("books", books);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Author not found"));
        }
    }

   public ResponseEntity<Map<String, Object>> fallbackBooks(Long authorId, Throwable t) {
    Optional<Author> author = authorService.getAuthor(authorId);
    
    if (author.isPresent()) {
        Map<String, Object> response = new HashMap<>();
        response.put("author", author.get());
        response.put("books", Collections.emptyList()); // Books service unavailable, so return empty list
        response.put("message", "Book Service is unavailable, please try again later");
        return ResponseEntity.ok(response);
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Author not found"));
    }
}

}

```
author ka app.proprties
```
server.port=8082
spring.application.name=AUTHOR-SERVICE
spring.datasource.url=jdbc:h2:mem:authordb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
management.endpoints.web.exposure.include=health
management.health.circuitbreakers.enabled=true



```

## Spring Security Overview (DAO AUTHENTICATION)
Spring Security is a powerful authentication and access control framework for Java applications, particularly Spring-based applications. The image outlines the authentication flow, showing how security filters, authentication managers, and providers work together.


### 1)Filter (Authentication)

This is the starting point where authentication begins.
When a user sends a request (e.g., logs in), the request goes through security filters.
Spring Security intercepts this request using authentication filters (UsernamePasswordAuthenticationFilter for form login).

### 2)Authentication Manager

The filter sends the authentication request (Authentication object) to the Authentication Manager.
It invokes the authenticate(Authentication auth) method to check credentials.

### 3)Provider Manager

The Provider Manager is responsible for delegating authentication to multiple Authentication Providers.
It checks credentials against different authentication providers

### 4) Authentication Providers

Multiple authentication providers exist (e.g., one for username/password, one for OAuth, etc.).
List of all providers will be queries to check if it can handles the authentication.
Each provider verifies credentials. If valid, it returns an authenticated Authentication object.

### 5)UserDetailsService

The authentication provider loads user details from the database using UserDetailsService.
It fetches user information and roles and validates them.

### 6)Setting Authentication in Security Context

If authentication is successful, Spring Security sets the authenticated user in the SecurityContext.
The user can now access secured resources.


#### ye hai internal flow smjhne ke liye agar asan bhasa mein samjhna hai to sidha smjho ki user jab login krta hai
to behind the scene username  request se phuchta hai  CustomUserDetailsService class ke ek method ke pass jo implement kart hai
UserDetailsService ke interface ko

to ek method hota hai UserDetailsService ke pass   "loadUserByUsername" isi ko  CustomUserDetailsService override karta hai
aur jo username jb user login karne time dalta hai wo is method ke parameter mein aata hai

aur us username se user entity ke repository ke  help se us username ke liye user ka pura details fetch hota database se hai aur ek baar User ka object
aagya to we can get user name,user password and all from database.
and then match kr skte ke password shi hai ya nhi ,jo user login krne time dala aur jo us user ka stored hai db mein

Spring Security needs a UserDetailsService implementation to fetch user details from the database.


```
 Load User from Database
We create a class that implements UserDetailsService:

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Convert roles into Spring Security format
        List<SimpleGrantedAuthority> authorities = user.getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                 // Convert role to authority,this will be used by spring to know the roles of user trying to login
                 //and grant access accordingly
                .collect(Collectors.toList());

        // Return Spring Security User object
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}

```
What Happens Here?

It fetches user details from the database.
It converts roles (STUDENT, TEACHER, ADMIN) into SimpleGrantedAuthority for Spring Security to use.
This method(loadUserByUsername) is called automatically when a user tries to log in.

### why - return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
We return this object because Spring Security expects UserDetails as the return type when it loads user information from the database.

when we return this with all the details of user fetched from database who is trying to login
This creates a Spring Security User object (UserDetails), which is then used by Spring Security's authentication system to verify the user and grant access.

The built-in User class (org.springframework.security.core.userdetails.User) implements UserDetails

```
public class User implements UserDetails {

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

   
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}


```
What Happens Step-by-Step?

1️⃣ User tries to log in by entering a username & password.

2️⃣ Spring Security automatically calls loadUserByUsername(username) inside UserDetailsService.

3️⃣ Your custom UserDetailsService fetches the user from the database (including roles/authorities).

4️⃣ It converts user roles to SimpleGrantedAuthority (needed for authorization by spring security)

5️⃣ It returns a UserDetails object (which contains the username, hashed password from db, and roles).

6 Spring Security needs a UserDetails object to authenticate the user.
The built-in User class (org.springframework.security.core.userdetails.User) implements UserDetails, so we use it here.
The returned object contains:
Username → Used for authentication.
Password (hashed) → Used for password matching.
Authorities (roles/permissions) → Used to check if the user can access specific endpoints.

## How Does Spring Security Use This?

1️⃣ UsernamePasswordAuthenticationFilter (Spring’s default login filter)

Calls AuthenticationManager to authenticate the user.

2️⃣ DaoAuthenticationProvider (Default provider for database authentication)

Calls UserDetailsService.loadUserByUsername(username).

3️⃣ Your CustomUserDetailsService fetches the user & roles,

Converts roles to authorities and returns a UserDetails object.

4️⃣ Password matching happens inside DaoAuthenticationProvider

PasswordEncoder.matches(rawPassword, storedHashedPassword).
If password is correct, the user is authenticated

5️⃣ Spring Security stores user details in the SecurityContext for authorization checks.

### Why Use org.springframework.security.core.userdetails.User Instead of UserEntity?

Spring Security requires a UserDetails object.

org.springframework.security.core.userdetails.User already implements UserDetails, so it's easy to use.

You could create a custom UserDetails implementation, but using Spring’s built-in User class is simpler.

```
How is Access Controlled?
Now, let’s define who can access what.

We configure Spring Security in a SecurityConfig class.
Define Security Rules

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN") // Only ADMIN can access
                .requestMatchers("/teacher/**").hasAnyRole("ADMIN", "TEACHER") // Teacher & Admin
                .requestMatchers("/student/**").hasAnyRole("STUDENT", "TEACHER", "ADMIN") // All students, teachers, and admins
                .anyRequest().authenticated() // Any other request needs authentication
            )
            .formLogin() // Enables form-based login
            .and()
            .logout().logoutUrl("/logout").logoutSuccessUrl("/login");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

```

### ✅ What Happens Here?

**/admin/** → Only ADMIN can access.

**/teacher/** → Only TEACHER and ADMIN can access.

**/student/** → STUDENT, TEACHER, and ADMIN can access.

Other requests require authentication.

If the user is not authorized, they get a 403 Forbidden error

###  What Happens When User Tries to Access a Protected Endpoint?
User sends a request (e.g., /admin/dashboard).

Spring Security checks if the user is logged in.

If logged in, it checks the user’s roles (from SimpleGrantedAuthority).

If role matches, request is allowed. ✅

If role doesn’t match, request is blocked (403 Forbidden). ❌


## summary
![img_1.png](img_1.png)


![img_2.png](img_2.png)



## eager vs lazy loading in hibernate
to be continued....



## OATH 2.0


# Fault tolerance
Need for Fault Tolerance
Fault Isolation
Network Latency
Deployment issues
Increased Complexity
Elasticity
tolerate external api failure


to handle fault we have the concept of resilience
some resilience techniques
1. retrying : retrying n no of times after failing
2. rate limiting : no of request going to a micro servie
3. bulk heads : dedicating a special resource to some service
4. circuit breaker : bcoz of service A , B should not be affected
5. fallbacks : users receive atleast some response if main service is down also
6. timeout :
7. graceful degrdation : resource extensive ca be limited and usage restricted

**Resilience4j** : a light-weight east to built fault tolerance library which help us implementing the above technique

Retry Module
-+ It's not uncommon for a network call or a method
invocation to fail temporarily
-+ We might want to retry the operation a few times
before giving up


RateLimiter
-+ We might have a service which can handle only a
certain number of requests in given time
RateLimiter module allows us to enforce
restrictions and protect our services from too many
requests


Bulkhead
Isolates failures and prevents them from
cascading through the system
Limit the amount of parallel executions or
concurrent calls to prevent system resources from
being exhausted

CircuitBreaker
Used to prevent a network or service failure from
cascading to other services
Circuit breaker 'trips' or opens and prevents
further calls to the service

working
- closed : calls are still flowing between the services
- open : calls are not flowing from one service to other

there is a threshold no suppose 5
i.e after the 5 request made by A service if no response is coming from B service we consider B to be dead

we can define a time X , after that time interval again A tries 5 time , if some resonse come then half opened
then again after X time 5 time if again response came then full open


we have to implement in each service these things


# ********** for actuator endpoint ***********************
management.endpoints.web.exposure.include = health
management.endpoint.health.show-details = always
management.health.circuitbreakers.enabled = true


# ********** for circuit breakers ***********************
resilience4j.circuitbreaker.instances.transactionBreaker.minimumNumberOfCalls=10
resilience4j.circuitbreaker.instances.transactionBreaker.slidingWindowSize=10
resilience4j.circuitbreaker.instances.transactionBreaker.permittedNumberOfCallsInHalfOpenState=5
resilience4j.circuitbreaker.instances.transactionBreaker.waitDurationInOpenState=10s
resilience4j.circuitbreaker.instances.transactionBreaker.failureRateThreshold=50

resilience4j.circuitbreaker.instances.transactionBreaker.register-health-indicator=true
resilience4j.circuitbreaker.instances.transactionBreaker.automatic-transition-from-open-to-half-open-enabled=true
resilience4j.circuitbreaker.instances.transactionBreaker.sliding-window-type=count_based

# ********** for retry mechanism ***********************
resilience4j.retry.instances.transactionBreaker.max-attempts=5
resilience4j.retry.instances.transactionBreaker.wait-duration = 2s

# ************ rate limiter mechanism ********************************
resilience4j.ratelimiter.instances.transactionBreaker.timeout-duration = 2
# it will allow 2 calls for every 4s
resilience4j.ratelimiter.instances.transactionBreaker.limit-refresh-period = 4s
resilience4j.ratelimiter.instances.transactionBreaker.limit-for-period = 2


http://localhost:5000/actuator/health  - in this link we can see the state of that service in which we configure the resiliance
if its state is open , clse , half close


# Packaging of the microservices
A package - bytecode of service files + dependent libraries + configuration
we create a package into a jar file . Other packing types are docker image , war

make sure java.exe and jar.exe are given in the env path
then to convert a service to a jar is either in power shell
jar -tf service-path
or in ide you can mvn package

# Dockerized our microservice
in this case we will be requiring the help of Spring profiles
i.e having different application.properties in case of different env




This paste expires in <1 hour. Public IP access. Share whatever you see with others in seconds with Context. Terms of ServiceReport this


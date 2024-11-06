// PlayerResult.java
@Entity
public class PlayerResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    
    private String name;
    private String hand;
    
    @ManyToOne
    private Event event;
    
    private boolean isWinner;
    
    // getters and setters
}
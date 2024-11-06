// Event.java
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<PlayerResult> players = new ArrayList<>();
    
    @OneToOne
    private PlayerResult winner;
    
    // getters and setters
}


@Service
@Transactional
public class GameService {
    
    private final EventRepository eventRepository;
    private final Set<String> registeredPlayers = new HashSet<>(Arrays.asList("Chance", "AliceGuo")); // Pre-registered players
    
    public GameService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public GameResultResponse startGame(StartGameRequest request) {
        // Validate players
        List<String> unregisteredPlayers = request.getPlayers().stream()
            .map(PlayerDTO::getName)
            .filter(name -> !registeredPlayers.contains(name))
            .collect(Collectors.toList());
            
        if (!unregisteredPlayers.isEmpty()) {
            log.warn("Unregistered players attempting to join: {}", unregisteredPlayers);
            throw new UnregisteredPlayersException("Some players are not registered: " + unregisteredPlayers);
        }

        // Create new game event
        Event event = new Event();
        
        // Simulate game and determine winner
        Game game = new Game(new DeckBuilder(), new HandIdentifier(), new WinningHandCalculator());
        
        // Add players to game
        request.getPlayers().forEach(playerDTO -> {
            PlayerResult result = new PlayerResult();
            result.setName(playerDTO.getName());
            result.setEvent(event);
            
            // Get player's final hand from game simulation
            Hand playerHand = game.identifyPlayerHand(game.getPlayers().stream()
                .filter(p -> p.getName().equals(playerDTO.getName()))
                .findFirst()
                .get());
                
            result.setHand(playerHand.toString());
            event.getPlayers().add(result);
        });

        // Set winner
        Optional<Player> winner = game.getWinner();
        if (winner.isPresent()) {
            PlayerResult winningResult = event.getPlayers().stream()
                .filter(p -> p.getName().equals(winner.get().getName()))
                .findFirst()
                .get();
            winningResult.setWinner(true);
            event.setWinner(winningResult);
        }

        // Save to database
        eventRepository.save(event);

        // Return response
        return createGameResultResponse(event);
    }

    public GameResultResponse getGameResult(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new GameNotFoundException("Game not found with id: " + eventId));
            
        return createGameResultResponse(event);
    }

    private GameResultResponse createGameResultResponse(Event event) {
        GameResultResponse response = new GameResultResponse();
        response.setEventId(event.getId());
        response.setPlayers(event.getPlayers().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList()));
        response.setWinner(event.getWinner().getName());
        return response;
    }
}
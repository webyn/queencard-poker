@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/start_game/{numberOfPlayers}")
    public ResponseEntity<GameResultResponse> startGame(
            @PathVariable int numberOfPlayers,
            @RequestBody StartGameRequest request) {
        
        if (request.getPlayers().size() != numberOfPlayers) {
            return ResponseEntity.badRequest().build();
        }
        
        GameResultResponse result = gameService.startGame(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<GameResultResponse> getGameResult(@PathVariable Long id) {
        GameResultResponse result = gameService.getGameResult(id);
        return ResponseEntity.ok(result);
    }
}
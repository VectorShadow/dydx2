package link.instructions;

public class UpdatePlayerMemoryInstructionDatum {
    //todo - store zone memory, such as visible and remembered tiles and revealed features, in UserAvatar(backend) and
    // PlayerSession(frontend).
    // When a player sees a tile for the first time or reveals a hidden terrain feature, the PlayerSession should be
    // updated and this instruction should be sent to the backend, which will update the UserAvatar. UserAvatar memory
    // is tied to current game zone - which needs to be added and updated. This zone and memory can then be loaded when
    // a user logs in to that avatar(if practicable - don't duplicate existing zones). In fact, why not create a field
    // that carried both a zone and a memory of it.
    // This is what is used by the backend to bulk update the player session memory, like gamezoneinstruction bulk
    // updates the frontend gamezone. We can probably then simply get rid of the frontend gamezone, instead storing it
    // in the player session. We can probably replace gamezoneupdate instructions with this new type.
    // We also need to make sure memory is kept in sync like updates, and use bulk updates to correct if not.
}

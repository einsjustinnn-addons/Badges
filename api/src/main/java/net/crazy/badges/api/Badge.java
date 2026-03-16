package net.crazy.badges.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Response;
import net.labymod.api.util.logging.Logging;
import java.util.ArrayList;
import java.util.UUID;

public class Badge {

  public static final int STAFF_BADGE = 1;
  public static final int TRANSLATOR_BADGE = 3;
  public static final int ONE_YEAR_STREAK = 9;
  public static final int TWO_YEAR_STREAK = 10;
  public static final int THREE_YEAR_STREAK = 11;
  public static final int HIGHEST_YEAR_STREAK = 13;

  private static final Logging LOGGER = Logging.getLogger();

  private final int id;
  private final UUID uuid;
  private final String name;
  private final String description;

  private final String iconUrl;
  private final Icon icon;

  private final String playersUrl;
  private final ArrayList<UUID> players = new ArrayList<>();

  public Badge(int id, UUID uuid, String name, String description) {
    this.id = id;
    this.uuid = uuid;
    this.name = name;
    this.description = description;
    this.iconUrl = String.format(
        "https://laby.net/texture/badge-small/%s.png",
        this.uuid.toString()
    );
    this.icon = Icon.url(this.iconUrl).resolution(50, 50);

    this.playersUrl = String.format("https://laby.net/api/badge/%s", this.id);

    this.updatePlayers();

  }

  public UUID getUniqueId() {
    return this.uuid;
  }

  public int getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public String getIconUrl() {
    return this.iconUrl;
  }

  public Icon getIcon() {
    return this.icon;
  }

  public ArrayList<UUID> players() {
    return this.players;
  }

  public void updatePlayers() {
    this.players.clear();
    Request.ofGson(JsonElement.class)
        .url(this.playersUrl)
        .async(true)
        .execute(this::handleResponse);
  }

  private void handleResponse(Response<JsonElement> response) {
    try {
      if (response.hasException()) {
        throw new IllegalStateException(response.exception());
      }

      if (response.isEmpty()) {
        throw new IllegalStateException("Response is empty");
      }

      JsonElement element = response.get();
      if (!element.isJsonArray()) {
        throw new IllegalStateException("Response is not an array");
      }

      JsonArray entries = element.getAsJsonArray();
      for (int index = 0; index < entries.size(); index++) {
        UUID player = UUID.fromString(entries.get(index).getAsString());
        this.players.add(player);
      }
    } catch (Exception exception) {
      LOGGER.warn("Failed to load badges", exception);
    }
  }
}

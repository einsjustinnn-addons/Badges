package net.crazy.badges.core.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.crazy.badges.api.Badge;
import net.crazy.badges.api.BadgeManager;
import net.labymod.api.models.Implements;
import net.labymod.api.util.io.web.request.Request;
import net.labymod.api.util.io.web.request.Response;
import org.jetbrains.annotations.NotNull;
import javax.inject.Singleton;

@Singleton
@Implements(BadgeManager.class)
public class DefaultBadgeManager implements BadgeManager {

  private static final String BADGES_ENDPOINT = "https://laby.net/api/v3/badges";

  private static final Logger LOGGER = Logger.getLogger(DefaultBadgeManager.class.getSimpleName());

  private final List<Badge> badges = new ArrayList<>();
  private final Map<UUID, List<Badge>> playerBadges = new HashMap<>();

  @Override
  @NotNull
  public List<Badge> getPlayerBadges(UUID uuid) {
    if(this.playerBadges.containsKey(uuid)) {
      return Collections.unmodifiableList(this.playerBadges.get(uuid));
    }
    List<Badge> playerBadges = new ArrayList<>();

    for(Badge badge : this.badges) {
      if (badge.getId() == Badge.STAFF_BADGE || badge.getId() == Badge.TRANSLATOR_BADGE) {
        continue;
      }
      if (badge.players().contains(uuid))
        playerBadges.add(badge);
    }

    this.playerBadges.put(uuid, playerBadges);
    return this.getPlayerBadges(uuid);
  }

  @Override
  public void removeFromPlayerCache(UUID uuid) {
    this.playerBadges.remove(uuid);
  }

  @Override
  public void clearPlayerCache() {
    this.playerBadges.clear();
  }

  @Override
  @NotNull
  public List<Badge> getBadges() {
    return Collections.unmodifiableList(this.badges);
  }

  @Override
  public void cacheBadges() {
    Request.ofGson(JsonElement.class)
        .url(BADGES_ENDPOINT)
        .handleErrorStream()
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

      this.badges.clear();
      JsonArray entries = element.getAsJsonArray();

      for (int i = 0; i < entries.size(); i++) {
        JsonObject object = entries.get(i).getAsJsonObject();

        int id = object.get("id").getAsInt();
        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        String name = object.get("name").getAsString();
        String description = object.get("description").getAsString();

        Badge badge = new Badge(id, uuid, name, description);
        this.badges.add(badge);
      }

      this.badges.sort(Comparator.comparingInt(Badge::getId));
    } catch (Exception exception) {
      LOGGER.log(Level.WARNING, "Failed to load badges", exception);
    }
  }
}

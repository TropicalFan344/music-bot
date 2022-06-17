package club.bottomservices.discordrpc.lib;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RichPresence {
    private final String state;
    private final String details;
    private final Timestamps timestamps;
    private final Assets assets;
    private final Party party;
    private final Secrets secrets;
    private final List<Button> buttons;

    public RichPresence(String state, String details, Timestamps timestamps, Assets assets, Party party, Secrets secrets, List<Button> buttons) {
        this.state = state;
        this.details = details;
        this.timestamps = timestamps;
        this.assets = assets;
        this.party = party;
        this.secrets = secrets;
        this.buttons = buttons;
    }

    public String state() {
        return state;
    }

    public String details() {
        return details;
    }

    public Timestamps timestamps() {
        return timestamps;
    }

    public Assets assets() {
        return assets;
    }

    public Party party() {
        return party;
    }

    public Secrets secrets() {
        return secrets;
    }

    public List<Button> buttons() {
        return buttons;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        RichPresence that = (RichPresence) obj;
        return Objects.equals(this.state, that.state) &&
                Objects.equals(this.details, that.details) &&
                Objects.equals(this.timestamps, that.timestamps) &&
                Objects.equals(this.assets, that.assets) &&
                Objects.equals(this.party, that.party) &&
                Objects.equals(this.secrets, that.secrets) &&
                Objects.equals(this.buttons, that.buttons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, details, timestamps, assets, party, secrets, buttons);
    }

    @Override
    public String toString() {
        return "RichPresence[" +
                "state=" + state + ", " +
                "details=" + details + ", " +
                "timestamps=" + timestamps + ", " +
                "assets=" + assets + ", " +
                "party=" + party + ", " +
                "secrets=" + secrets + ", " +
                "buttons=" + buttons + ']';
    }
    public static class Builder {
        private String state = null;
        private String details = null;

        private Long start = null;
        private Long end = null;

        private String largeImage = null;
        private String largeText = null;
        private String smallImage = null;
        private String smallText = null;

        private String id = null;
        private Integer size = null;
        private Integer max = null;

        private String join = null;
        private String spectate = null;
        private String match = null;

        private List<String> buttons = null;
        private List<String> buttonUrls = null;

        public Builder setText(@Nullable String details, @Nullable String state) {
            this.state = state;
            this.details = details;
            return this;
        }

        /**
         * Unix timestamps (such as from {@link System#currentTimeMillis()} divided by 1000)
         * @return This builder
         */
        public Builder setTimestamps(@Nullable Long start, @Nullable Long end) {
            this.start = start;
            this.end = end;
            return this;
        }

        /**
         * Nullability note: The built {@link RichPresence} instance's assets'
         * largeImage will only be null if its largeText is also null, and vice versa,
         * this applies equivalently to the small values.
         * Never use empty strings as parameters here, always null.
         * @param largeImage Key of the large image used by your discord application
         * @param largeText Hover text of the large image
         * @param smallImage Key of the small image used by your discord application
         * @param smallText Hover text of the small image
         * @return This builder
         */
        public Builder setAssets(@Nullable String largeImage,
                                 @Nullable String largeText,
                                 @Nullable String smallImage,
                                 @Nullable String smallText) {
            this.largeImage = largeImage;
            this.largeText = largeText;
            this.smallImage = smallImage;
            this.smallText = smallText;
            return this;
        }

        /**
         * @param id The party id, should only be null when removing party info
         * @return This builder
         */
        public Builder setPartyInfo(@Nullable String id, int size, int max) {
            this.id = id;
            this.size = size;
            this.max = max;
            return this;
        }

        public Builder setSecrets(@Nullable String join, @Nullable String spectate, @Nullable String match) {
            this.join = join;
            this.spectate = spectate;
            this.match = match;
            return this;
        }

        public Builder addButton(@Nonnull String name, @Nonnull String url) {
            if (buttons == null) {
                buttons = new ArrayList<>();
                buttonUrls = new ArrayList<>();
            }
            buttons.add(name);
            buttonUrls.add(url);
            return this;
        }

        /**
         * Builds a {@link RichPresence} from the data in this builder
         * @throws IllegalArgumentException If any of the image keys or texts was an empty string, discord does not accept those
         * @return The built {@link RichPresence}
         */
        public RichPresence build() {
            Timestamps timestamps = new Timestamps(start, end);
            Assets assets = null;
            // Bad code, fix later
            if ((largeImage != null && largeText != null) || (smallImage != null && smallText != null)) {
                if ((largeImage != null && (largeImage.isEmpty() || largeText.isEmpty())) // If largeImage isn't null, so isn't largeText
                        || (smallImage != null && (smallImage.isEmpty() || smallText.isEmpty()))) {
                    throw new IllegalArgumentException("RichPresence must not be built with empty image strings");
                }
                assets = new Assets(largeImage, largeText, smallImage, smallText);
            }

            Party party = null;
            if (id != null && size != null && max != null) {
                party = new Party(id, new int[]{size, max});
            }

            Secrets secrets = null;
            if (join != null || spectate != null || match != null) {
                secrets = new Secrets(join, spectate, match);
            }

            List<Button> buttons = new ArrayList<>();
            if (this.buttons != null) {
                for (int i = 0; i < this.buttons.size(); i++) {
                    buttons.add(new Button(this.buttons.get(i), buttonUrls.get(i)));
                }
            }
            buttons = buttons.isEmpty() ? null : buttons;
            return new RichPresence(state, details, timestamps, assets, party, secrets, buttons);
        }
    }
    public static class Assets {
        @SerializedName("large_image")
        private final String largeImage;
        @SerializedName("large_text")
        private final String largeText;
        @SerializedName("small_image")
        private final String smallImage;
        @SerializedName("small_text")
        private final String smallText;

        Assets(String largeImage, String largeText, String smallImage, String smallText) {
            this.largeImage = largeImage;
            this.largeText = largeText;
            this.smallImage = smallImage;
            this.smallText = smallText;
        }

        public String largeImage() {
            return largeImage;
        }

        public String largeText() {
            return largeText;
        }

        public String smallImage() {
            return smallImage;
        }

        public String smallText() {
            return smallText;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Assets that = (Assets) obj;
            return Objects.equals(this.largeImage, that.largeImage) &&
                    Objects.equals(this.largeText, that.largeText) &&
                    Objects.equals(this.smallImage, that.smallImage) &&
                    Objects.equals(this.smallText, that.smallText);
        }

        @Override
        public int hashCode() {
            return Objects.hash(largeImage, largeText, smallImage, smallText);
        }

        @Override
        public String toString() {
            return "Assets[" +
                    "largeImage=" + largeImage + ", " +
                    "largeText=" + largeText + ", " +
                    "smallImage=" + smallImage + ", " +
                    "smallText=" + smallText + ']';
        }

    }

    public static class Button {
        private final String label;
        private final String url;

        Button(String label, String url) {
            this.label = label;
            this.url = url;
        }

        public String label() {
            return label;
        }

        public String url() {
            return url;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Button that = (Button) obj;
            return Objects.equals(this.label, that.label) &&
                    Objects.equals(this.url, that.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label, url);
        }

        @Override
        public String toString() {
            return "Button[" +
                    "label=" + label + ", " +
                    "url=" + url + ']';
        }

    }

    public static class Timestamps {
        private final Long start;
        private final Long end;

        public Timestamps(Long start, Long end) {
            this.start = start;
            this.end = end;
        }

        public Long start() {
            return start;
        }

        public Long end() {
            return end;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Timestamps that = (Timestamps) obj;
            return Objects.equals(this.start, that.start) &&
                    Objects.equals(this.end, that.end);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public String toString() {
            return "Timestamps[" +
                    "start=" + start + ", " +
                    "end=" + end + ']';
        }

    }


    public static class Party {
        private final String id;
        private final int[] size;

        public Party(String id, int[] size) {
            this.id = id;
            this.size = size;
        }

        public String id() {
            return id;
        }

        public int[] size() {
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Party that = (Party) obj;
            return Objects.equals(this.id, that.id) &&
                    Objects.equals(this.size, that.size);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, size);
        }

        @Override
        public String toString() {
            return "Party[" +
                    "id=" + id + ", " +
                    "size=" + size + ']';
        }

    }

    public static class Secrets {
        private final String join;
        private final String spectate;
        private final String match;

        public Secrets(String join, String spectate, String match) {
            this.join = join;
            this.spectate = spectate;
            this.match = match;
        }

        public String join() {
            return join;
        }

        public String spectate() {
            return spectate;
        }

        public String match() {
            return match;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Secrets that = (Secrets) obj;
            return Objects.equals(this.join, that.join) &&
                    Objects.equals(this.spectate, that.spectate) &&
                    Objects.equals(this.match, that.match);
        }

        @Override
        public int hashCode() {
            return Objects.hash(join, spectate, match);
        }

        @Override
        public String toString() {
            return "Secrets[" +
                    "join=" + join + ", " +
                    "spectate=" + spectate + ", " +
                    "match=" + match + ']';
        }

    }
}

package org.ubjson;

import java.io.FileOutputStream;
import java.io.IOException;

import org.ubjson.io.UBJInputStream;
import org.ubjson.io.UBJOutputStream;

public class TwitterTimelineMarshaller {
	/*
	 * Used to write out the test UBJ file handled by this class. Typically only
	 * needed by author to write a new test UBJ file if the file format changes
	 * due to an update to the POJO or marshaling logic.
	 */
	public static void main(String[] args) throws IOException {
		serialize(new TwitterTimeline(), new UBJOutputStream(
				new FileOutputStream("TwitterTimeline.ubj")));
	}

	public static void serialize(TwitterTimeline tt, UBJOutputStream out)
			throws IOException {
		// root obj
		out.writeObjectHeader(21);

		out.writeString("id_str");
		out.writeString(tt.id_str);

		out.writeString("retweet_count");
		out.writeInt32(tt.retweet_count);

		out.writeString("in_reply_to_screen_name");
		out.writeNull();

		out.writeString("in_reply_to_user_id");
		out.writeNull();

		out.writeString("truncated");
		out.writeBoolean(tt.truncated);

		out.writeString("retweeted");
		out.writeBoolean(tt.retweeted);

		out.writeString("possibly_sensitive");
		out.writeBoolean(tt.possibly_sensitive);

		out.writeString("in_reply_to_status_id_str");
		out.writeNull();

		// Entities start
		out.writeString("entities");
		out.writeObjectHeader(3);

		// Entities:URL start
		out.writeString("urls");
		out.writeObjectHeader(4);

		out.writeString("url");
		out.writeString(tt.entities.urls.url);

		out.writeString("display_url");
		out.writeString(tt.entities.urls.display_url);

		out.writeString("indices");
		out.writeArrayHeader(2);
		out.writeInt32(tt.entities.urls.indices[0]);
		out.writeInt32(tt.entities.urls.indices[1]);

		out.writeString("expanded_url");
		out.writeString(tt.entities.urls.expanded_url);
		// Entities:URL end

		out.writeString("hashtags");
		out.writeArrayHeader(0);

		out.writeString("user_mentions");
		out.writeArrayHeader(0);
		// Entities end

		out.writeString("geo");
		out.writeNull();

		out.writeString("place");
		out.writeNull();

		out.writeString("coordinates");
		out.writeNull();

		out.writeString("created_at");
		out.writeString(tt.created_at);

		out.writeString("in_reply_to_user_id_str");
		out.writeNull();

		// User start
		out.writeString("user");
		out.writeObjectHeader(38);

		out.writeString("id_str");
		out.writeString(tt.user.id_str);

		out.writeString("profile_link_color");
		out.writeString(tt.user.profile_link_color);

		out.writeString("protectedd");
		out.writeBoolean(tt.user.protectedd);

		out.writeString("url");
		out.writeString(tt.user.url);

		out.writeString("screen_name");
		out.writeString(tt.user.screen_name);

		out.writeString("statuses_count");
		out.writeInt32(tt.user.statuses_count);

		out.writeString("profile_image_url");
		out.writeString(tt.user.profile_image_url);

		out.writeString("name");
		out.writeString(tt.user.name);

		out.writeString("default_profile_image");
		out.writeBoolean(tt.user.default_profile_image);

		out.writeString("default_profile");
		out.writeBoolean(tt.user.default_profile);

		out.writeString("profile_background_color");
		out.writeString(tt.user.profile_background_color);

		out.writeString("lang");
		out.writeString(tt.user.lang);

		out.writeString("profile_background_tile");
		out.writeBoolean(tt.user.profile_background_tile);

		out.writeString("utc_offset");
		out.writeInt32(tt.user.utc_offset);

		out.writeString("description");
		out.writeString(tt.user.description);

		out.writeString("is_translator");
		out.writeBoolean(tt.user.is_translator);

		out.writeString("show_all_inline_media");
		out.writeBoolean(tt.user.show_all_inline_media);

		out.writeString("contributors_enabled");
		out.writeBoolean(tt.user.contributors_enabled);

		out.writeString("profile_background_image_url_https");
		out.writeString(tt.user.profile_background_image_url_https);

		out.writeString("created_at");
		out.writeString(tt.user.created_at);

		out.writeString("profile_sidebar_fill_color");
		out.writeString(tt.user.profile_sidebar_fill_color);

		out.writeString("follow_request_sent");
		out.writeBoolean(tt.user.follow_request_sent);

		out.writeString("friends_count");
		out.writeInt32(tt.user.friends_count);

		out.writeString("followers_count");
		out.writeInt32(tt.user.followers_count);

		out.writeString("time_zone");
		out.writeString(tt.user.time_zone);

		out.writeString("favourites_count");
		out.writeInt32(tt.user.favourites_count);

		out.writeString("profile_sidebar_border_color");
		out.writeString(tt.user.profile_sidebar_border_color);

		out.writeString("profile_image_url_https");
		out.writeString(tt.user.profile_image_url_https);

		out.writeString("following");
		out.writeBoolean(tt.user.following);

		out.writeString("geo_enabled");
		out.writeBoolean(tt.user.geo_enabled);

		out.writeString("notifications");
		out.writeBoolean(tt.user.notifications);

		out.writeString("profile_use_background_image");
		out.writeBoolean(tt.user.profile_use_background_image);

		out.writeString("listed_count");
		out.writeInt32(tt.user.listed_count);

		out.writeString("verified");
		out.writeBoolean(tt.user.verified);

		out.writeString("profile_text_color");
		out.writeString(tt.user.profile_text_color);

		out.writeString("location");
		out.writeString(tt.user.location);

		out.writeString("id");
		out.writeInt32(tt.user.id);

		out.writeString("profile_background_image_url");
		out.writeString(tt.user.profile_background_image_url);
		// User end

		out.writeString("contributors");
		out.writeNull();

		out.writeString("source");
		out.writeString(tt.source);

		out.writeString("in_reply_to_status_id");
		out.writeNull();

		out.writeString("favorited");
		out.writeBoolean(tt.favorited);

		out.writeString("id");
		out.writeInt64(tt.id);

		out.writeString("text");
		out.writeString(tt.text);
	}

	public static TwitterTimeline deserialize(UBJInputStream in)
			throws IOException {
		TwitterTimeline tt = new TwitterTimeline();

		// root obj
		in.readObjectLength();

		in.readStringAsChars();
		tt.id_str = in.readString();

		in.readStringAsChars();
		tt.retweet_count = in.readInt32();

		in.readStringAsChars();
		// tt.in_reply_to_screen_name =
		in.readNull();

		in.readStringAsChars();
		// tt.in_reply_to_user_id =
		in.readNull();

		in.readStringAsChars();
		tt.truncated = in.readBoolean();

		in.readStringAsChars();
		tt.retweeted = in.readBoolean();

		in.readStringAsChars();
		tt.possibly_sensitive = in.readBoolean();

		in.readStringAsChars();
		// tt.in_reply_to_status_id_str =
		in.readNull();

		// Entities
		in.readStringAsChars();
		in.readObjectLength();

		// Entities:URL
		in.readStringAsChars();
		in.readObjectLength();

		in.readStringAsChars();
		tt.entities.urls.url = in.readString();

		in.readStringAsChars();
		tt.entities.urls.display_url = in.readString();

		in.readStringAsChars();
		in.readArrayLength();
		tt.entities.urls.indices[0] = in.readInt32();
		tt.entities.urls.indices[1] = in.readInt32();

		in.readStringAsChars();
		tt.entities.urls.expanded_url = in.readString();
		// Entities:URL end

		in.readStringAsChars();
		in.readArrayLength();

		in.readStringAsChars();
		in.readArrayLength();
		// Entities end

		in.readStringAsChars();
		// tt.geo =
		in.readNull();

		in.readStringAsChars();
		// tt.place =
		in.readNull();

		in.readStringAsChars();
		// tt.coordinates =
		in.readNull();

		in.readStringAsChars();
		tt.created_at = in.readString();

		in.readStringAsChars();
		// tt.in_reply_to_user_id_str =
		in.readNull();

		// User
		in.readStringAsChars();
		in.readObjectLength();

		in.readStringAsChars();
		tt.user.id_str = in.readString();

		in.readStringAsChars();
		tt.user.profile_link_color = in.readString();

		in.readStringAsChars();
		tt.user.protectedd = in.readBoolean();

		in.readStringAsChars();
		tt.user.url = in.readString();

		in.readStringAsChars();
		tt.user.screen_name = in.readString();

		in.readStringAsChars();
		tt.user.statuses_count = in.readInt32();

		in.readStringAsChars();
		tt.user.profile_image_url = in.readString();

		in.readStringAsChars();
		tt.user.name = in.readString();

		in.readStringAsChars();
		tt.user.default_profile_image = in.readBoolean();

		in.readStringAsChars();
		tt.user.default_profile = in.readBoolean();

		in.readStringAsChars();
		tt.user.profile_background_color = in.readString();

		in.readStringAsChars();
		tt.user.lang = in.readString();

		in.readStringAsChars();
		tt.user.profile_background_tile = in.readBoolean();

		in.readStringAsChars();
		tt.user.utc_offset = in.readInt32();

		in.readStringAsChars();
		tt.user.description = in.readString();

		in.readStringAsChars();
		tt.user.is_translator = in.readBoolean();

		in.readStringAsChars();
		tt.user.show_all_inline_media = in.readBoolean();

		in.readStringAsChars();
		tt.user.contributors_enabled = in.readBoolean();

		in.readStringAsChars();
		tt.user.profile_background_image_url_https = in.readString();

		in.readStringAsChars();
		tt.user.created_at = in.readString();

		in.readStringAsChars();
		tt.user.profile_sidebar_fill_color = in.readString();

		in.readStringAsChars();
		tt.user.follow_request_sent = in.readBoolean();

		in.readStringAsChars();
		tt.user.friends_count = in.readInt32();

		in.readStringAsChars();
		tt.user.followers_count = in.readInt32();

		in.readStringAsChars();
		tt.user.time_zone = in.readString();

		in.readStringAsChars();
		tt.user.favourites_count = in.readInt32();

		in.readStringAsChars();
		tt.user.profile_sidebar_border_color = in.readString();

		in.readStringAsChars();
		tt.user.profile_image_url_https = in.readString();

		in.readStringAsChars();
		tt.user.following = in.readBoolean();

		in.readStringAsChars();
		tt.user.geo_enabled = in.readBoolean();

		in.readStringAsChars();
		tt.user.notifications = in.readBoolean();

		in.readStringAsChars();
		tt.user.profile_use_background_image = in.readBoolean();

		in.readStringAsChars();
		tt.user.listed_count = in.readInt32();

		in.readStringAsChars();
		tt.user.verified = in.readBoolean();

		in.readStringAsChars();
		tt.user.profile_text_color = in.readString();

		in.readStringAsChars();
		tt.user.location = in.readString();

		in.readStringAsChars();
		tt.user.id = in.readInt32();

		in.readStringAsChars();
		tt.user.profile_background_image_url = in.readString();
		// User end

		in.readStringAsChars();
		// tt.contributors =
		in.readNull();

		in.readStringAsChars();
		tt.source = in.readString();

		in.readStringAsChars();
		// tt.in_reply_to_status_id =
		in.readNull();

		in.readStringAsChars();
		tt.favorited = in.readBoolean();

		in.readStringAsChars();
		tt.id = in.readInt64();

		in.readStringAsChars();
		tt.text = in.readString();

		return tt;
	}
}
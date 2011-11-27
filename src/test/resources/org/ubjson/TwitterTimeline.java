package org.ubjson;

import java.util.Arrays;

public class TwitterTimeline {
	public String id_str = "121769183821312000";
	public int retweet_count = 0;
	public String in_reply_to_screen_name = null;
	public String in_reply_to_user_id = null;
	public boolean truncated = false;
	public boolean retweeted = false;
	public boolean possibly_sensitive = false;
	public String in_reply_to_status_id_str = null;

	public Entities entities = new Entities();

	public String geo = null;
	public String place = null;
	public String coordinates = null;
	public String created_at = "Thu Oct 06 02:10:10 +0000 2011";
	public String in_reply_to_user_id_str = null;

	public User user = new User();

	public String contributors = null;

	public String source = "\u003Ca href=\"http:\\\\dlvr.it\" rel=\"nofollow\"\u003Edlvr.it\u003C\\a\u003E";
	public String in_reply_to_status_id = null;
	public boolean favorited = false;
	public long id = 121769183821312000L;
	public String text = "Apple CEO's message to employees http:\\\\t.co\\wtioKkFS";

	public class Entities {
		public URL urls = new URL();
		public String[] hashtags = {};
		public String[] user_mentions = {};

		public class URL {
			public String url = "http:\\\\t.co\\wtioKkFS";
			public String display_url = "dlvr.it\\pWQy2";
			public int[] indices = { 33, 53 };
			public String expanded_url = "http:\\\\dlvr.it\\pWQy2";

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((display_url == null) ? 0 : display_url.hashCode());
				result = prime
						* result
						+ ((expanded_url == null) ? 0 : expanded_url.hashCode());
				result = prime * result + Arrays.hashCode(indices);
				result = prime * result + ((url == null) ? 0 : url.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				URL other = (URL) obj;
				if (display_url == null) {
					if (other.display_url != null)
						return false;
				} else if (!display_url.equals(other.display_url))
					return false;
				if (expanded_url == null) {
					if (other.expanded_url != null)
						return false;
				} else if (!expanded_url.equals(other.expanded_url))
					return false;
				if (!Arrays.equals(indices, other.indices))
					return false;
				if (url == null) {
					if (other.url != null)
						return false;
				} else if (!url.equals(other.url))
					return false;
				return true;
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(hashtags);
			result = prime * result + ((urls == null) ? 0 : urls.hashCode());
			result = prime * result + Arrays.hashCode(user_mentions);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Entities other = (Entities) obj;
			if (!Arrays.equals(hashtags, other.hashtags))
				return false;
			if (urls == null) {
				if (other.urls != null)
					return false;
			} else if (!urls.equals(other.urls))
				return false;
			if (!Arrays.equals(user_mentions, other.user_mentions))
				return false;
			return true;
		}
	}

	public class User {
		public String id_str = "77029015";
		public String profile_link_color = "009999";
		public boolean protectedd = false;
		public String url = "http:\\\\www.techday.co.nz\\";
		public String screen_name = "techdaynz";
		public int statuses_count = 5144;
		public String profile_image_url = "http:\\\\a0.twimg.com\\profile_images\\1479058408\\techday_48_normal.jpg";
		public String name = "TechDay";
		public boolean default_profile_image = false;
		public boolean default_profile = false;
		public String profile_background_color = "131516";
		public String lang = "en";
		public boolean profile_background_tile = false;
		public int utc_offset = 43200;
		public String description = "";
		public boolean is_translator = false;
		public boolean show_all_inline_media = false;
		public boolean contributors_enabled = false;
		public String profile_background_image_url_https = "https:\\\\si0.twimg.com\\profile_background_images\\75893948\\Techday_Background.jpg";
		public String created_at = "Thu Sep 24 20:02:01 +0000 2009";
		public String profile_sidebar_fill_color = "efefef";
		public boolean follow_request_sent = false;
		public int friends_count = 3215;
		public int followers_count = 3149;
		public String time_zone = "Auckland";
		public int favourites_count = 0;
		public String profile_sidebar_border_color = "eeeeee";
		public String profile_image_url_https = "https:\\\\si0.twimg.com\\profile_images\\1479058408\\techday_48_normal.jpg";
		public boolean following = false;
		public boolean geo_enabled = false;
		public boolean notifications = false;
		public boolean profile_use_background_image = true;
		public int listed_count = 151;
		public boolean verified = false;
		public String profile_text_color = "333333";
		public String location = "Ponsonby, Auckland, NZ";
		public int id = 77029015;
		public String profile_background_image_url = "http:\\\\a0.twimg.com\\profile_background_images\\75893948\\Techday_Background.jpg";

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (contributors_enabled ? 1231 : 1237);
			result = prime * result
					+ ((created_at == null) ? 0 : created_at.hashCode());
			result = prime * result + (default_profile ? 1231 : 1237);
			result = prime * result + (default_profile_image ? 1231 : 1237);
			result = prime * result
					+ ((description == null) ? 0 : description.hashCode());
			result = prime * result + favourites_count;
			result = prime * result + (follow_request_sent ? 1231 : 1237);
			result = prime * result + followers_count;
			result = prime * result + (following ? 1231 : 1237);
			result = prime * result + friends_count;
			result = prime * result + (geo_enabled ? 1231 : 1237);
			result = prime * result + id;
			result = prime * result
					+ ((id_str == null) ? 0 : id_str.hashCode());
			result = prime * result + (is_translator ? 1231 : 1237);
			result = prime * result + ((lang == null) ? 0 : lang.hashCode());
			result = prime * result + listed_count;
			result = prime * result
					+ ((location == null) ? 0 : location.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + (notifications ? 1231 : 1237);
			result = prime
					* result
					+ ((profile_background_color == null) ? 0
							: profile_background_color.hashCode());
			result = prime
					* result
					+ ((profile_background_image_url == null) ? 0
							: profile_background_image_url.hashCode());
			result = prime
					* result
					+ ((profile_background_image_url_https == null) ? 0
							: profile_background_image_url_https.hashCode());
			result = prime * result + (profile_background_tile ? 1231 : 1237);
			result = prime
					* result
					+ ((profile_image_url == null) ? 0 : profile_image_url
							.hashCode());
			result = prime
					* result
					+ ((profile_image_url_https == null) ? 0
							: profile_image_url_https.hashCode());
			result = prime
					* result
					+ ((profile_link_color == null) ? 0 : profile_link_color
							.hashCode());
			result = prime
					* result
					+ ((profile_sidebar_border_color == null) ? 0
							: profile_sidebar_border_color.hashCode());
			result = prime
					* result
					+ ((profile_sidebar_fill_color == null) ? 0
							: profile_sidebar_fill_color.hashCode());
			result = prime
					* result
					+ ((profile_text_color == null) ? 0 : profile_text_color
							.hashCode());
			result = prime * result
					+ (profile_use_background_image ? 1231 : 1237);
			result = prime * result + (protectedd ? 1231 : 1237);
			result = prime * result
					+ ((screen_name == null) ? 0 : screen_name.hashCode());
			result = prime * result + (show_all_inline_media ? 1231 : 1237);
			result = prime * result + statuses_count;
			result = prime * result
					+ ((time_zone == null) ? 0 : time_zone.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			result = prime * result + utc_offset;
			result = prime * result + (verified ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			User other = (User) obj;
			if (contributors_enabled != other.contributors_enabled)
				return false;
			if (created_at == null) {
				if (other.created_at != null)
					return false;
			} else if (!created_at.equals(other.created_at))
				return false;
			if (default_profile != other.default_profile)
				return false;
			if (default_profile_image != other.default_profile_image)
				return false;
			if (description == null) {
				if (other.description != null)
					return false;
			} else if (!description.equals(other.description))
				return false;
			if (favourites_count != other.favourites_count)
				return false;
			if (follow_request_sent != other.follow_request_sent)
				return false;
			if (followers_count != other.followers_count)
				return false;
			if (following != other.following)
				return false;
			if (friends_count != other.friends_count)
				return false;
			if (geo_enabled != other.geo_enabled)
				return false;
			if (id != other.id)
				return false;
			if (id_str == null) {
				if (other.id_str != null)
					return false;
			} else if (!id_str.equals(other.id_str))
				return false;
			if (is_translator != other.is_translator)
				return false;
			if (lang == null) {
				if (other.lang != null)
					return false;
			} else if (!lang.equals(other.lang))
				return false;
			if (listed_count != other.listed_count)
				return false;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (notifications != other.notifications)
				return false;
			if (profile_background_color == null) {
				if (other.profile_background_color != null)
					return false;
			} else if (!profile_background_color
					.equals(other.profile_background_color))
				return false;
			if (profile_background_image_url == null) {
				if (other.profile_background_image_url != null)
					return false;
			} else if (!profile_background_image_url
					.equals(other.profile_background_image_url))
				return false;
			if (profile_background_image_url_https == null) {
				if (other.profile_background_image_url_https != null)
					return false;
			} else if (!profile_background_image_url_https
					.equals(other.profile_background_image_url_https))
				return false;
			if (profile_background_tile != other.profile_background_tile)
				return false;
			if (profile_image_url == null) {
				if (other.profile_image_url != null)
					return false;
			} else if (!profile_image_url.equals(other.profile_image_url))
				return false;
			if (profile_image_url_https == null) {
				if (other.profile_image_url_https != null)
					return false;
			} else if (!profile_image_url_https
					.equals(other.profile_image_url_https))
				return false;
			if (profile_link_color == null) {
				if (other.profile_link_color != null)
					return false;
			} else if (!profile_link_color.equals(other.profile_link_color))
				return false;
			if (profile_sidebar_border_color == null) {
				if (other.profile_sidebar_border_color != null)
					return false;
			} else if (!profile_sidebar_border_color
					.equals(other.profile_sidebar_border_color))
				return false;
			if (profile_sidebar_fill_color == null) {
				if (other.profile_sidebar_fill_color != null)
					return false;
			} else if (!profile_sidebar_fill_color
					.equals(other.profile_sidebar_fill_color))
				return false;
			if (profile_text_color == null) {
				if (other.profile_text_color != null)
					return false;
			} else if (!profile_text_color.equals(other.profile_text_color))
				return false;
			if (profile_use_background_image != other.profile_use_background_image)
				return false;
			if (protectedd != other.protectedd)
				return false;
			if (screen_name == null) {
				if (other.screen_name != null)
					return false;
			} else if (!screen_name.equals(other.screen_name))
				return false;
			if (show_all_inline_media != other.show_all_inline_media)
				return false;
			if (statuses_count != other.statuses_count)
				return false;
			if (time_zone == null) {
				if (other.time_zone != null)
					return false;
			} else if (!time_zone.equals(other.time_zone))
				return false;
			if (url == null) {
				if (other.url != null)
					return false;
			} else if (!url.equals(other.url))
				return false;
			if (utc_offset != other.utc_offset)
				return false;
			if (verified != other.verified)
				return false;
			return true;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contributors == null) ? 0 : contributors.hashCode());
		result = prime * result
				+ ((coordinates == null) ? 0 : coordinates.hashCode());
		result = prime * result
				+ ((created_at == null) ? 0 : created_at.hashCode());
		result = prime * result
				+ ((entities == null) ? 0 : entities.hashCode());
		result = prime * result + (favorited ? 1231 : 1237);
		result = prime * result + ((geo == null) ? 0 : geo.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((id_str == null) ? 0 : id_str.hashCode());
		result = prime
				* result
				+ ((in_reply_to_screen_name == null) ? 0
						: in_reply_to_screen_name.hashCode());
		result = prime
				* result
				+ ((in_reply_to_status_id == null) ? 0 : in_reply_to_status_id
						.hashCode());
		result = prime
				* result
				+ ((in_reply_to_status_id_str == null) ? 0
						: in_reply_to_status_id_str.hashCode());
		result = prime
				* result
				+ ((in_reply_to_user_id == null) ? 0 : in_reply_to_user_id
						.hashCode());
		result = prime
				* result
				+ ((in_reply_to_user_id_str == null) ? 0
						: in_reply_to_user_id_str.hashCode());
		result = prime * result + ((place == null) ? 0 : place.hashCode());
		result = prime * result + (possibly_sensitive ? 1231 : 1237);
		result = prime * result + retweet_count;
		result = prime * result + (retweeted ? 1231 : 1237);
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + (truncated ? 1231 : 1237);
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TwitterTimeline other = (TwitterTimeline) obj;
		if (contributors == null) {
			if (other.contributors != null)
				return false;
		} else if (!contributors.equals(other.contributors))
			return false;
		if (coordinates == null) {
			if (other.coordinates != null)
				return false;
		} else if (!coordinates.equals(other.coordinates))
			return false;
		if (created_at == null) {
			if (other.created_at != null)
				return false;
		} else if (!created_at.equals(other.created_at))
			return false;
		if (entities == null) {
			if (other.entities != null)
				return false;
		} else if (!entities.equals(other.entities))
			return false;
		if (favorited != other.favorited)
			return false;
		if (geo == null) {
			if (other.geo != null)
				return false;
		} else if (!geo.equals(other.geo))
			return false;
		if (id != other.id)
			return false;
		if (id_str == null) {
			if (other.id_str != null)
				return false;
		} else if (!id_str.equals(other.id_str))
			return false;
		if (in_reply_to_screen_name == null) {
			if (other.in_reply_to_screen_name != null)
				return false;
		} else if (!in_reply_to_screen_name
				.equals(other.in_reply_to_screen_name))
			return false;
		if (in_reply_to_status_id == null) {
			if (other.in_reply_to_status_id != null)
				return false;
		} else if (!in_reply_to_status_id.equals(other.in_reply_to_status_id))
			return false;
		if (in_reply_to_status_id_str == null) {
			if (other.in_reply_to_status_id_str != null)
				return false;
		} else if (!in_reply_to_status_id_str
				.equals(other.in_reply_to_status_id_str))
			return false;
		if (in_reply_to_user_id == null) {
			if (other.in_reply_to_user_id != null)
				return false;
		} else if (!in_reply_to_user_id.equals(other.in_reply_to_user_id))
			return false;
		if (in_reply_to_user_id_str == null) {
			if (other.in_reply_to_user_id_str != null)
				return false;
		} else if (!in_reply_to_user_id_str
				.equals(other.in_reply_to_user_id_str))
			return false;
		if (place == null) {
			if (other.place != null)
				return false;
		} else if (!place.equals(other.place))
			return false;
		if (possibly_sensitive != other.possibly_sensitive)
			return false;
		if (retweet_count != other.retweet_count)
			return false;
		if (retweeted != other.retweeted)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (truncated != other.truncated)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}
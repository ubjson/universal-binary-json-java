package org.ubjson;

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
	}
}
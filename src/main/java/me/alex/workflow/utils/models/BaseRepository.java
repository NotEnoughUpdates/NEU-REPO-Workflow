package me.alex.workflow.utils.models;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BaseRepository(Repo repo, User user) {
	public static final Codec<BaseRepository> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Repo.CODEC.fieldOf("repo").forGetter(BaseRepository::repo),
		User.CODEC.fieldOf("user").forGetter(BaseRepository::user)
	).apply(instance, BaseRepository::new));

	public String ownerName() {
		return user.name();
	}

	public String repoName() {
		return repo.name();
	}

	public record Repo(String name) {
		public static final Codec<Repo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(Repo::name)
		).apply(instance, Repo::new));
	}

	public record User(String name) {
		public static final Codec<User> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("login").forGetter(User::name)
		).apply(instance, User::new));
	}
}

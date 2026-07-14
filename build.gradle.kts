plugins {
	id("java")
	id("application")
}

group = "me.alex"
version = "1.0-SNAPSHOT"

repositories {
	maven("https://maven.operationpotato.com/mirror")

	mavenCentral()
	exclusiveContent {
		forRepository {
			ivy("https://piston-data.mojang.com/v1/objects/") {
				patternLayout {
					artifact("[revision]/[artifact].[ext]")
				}
				metadataSources { artifact() }
			}
		}
		filter {
			includeModule("com.mojang", "client")
		}
	}
	maven("https://libraries.minecraft.net")
}

dependencies {
	implementation(libs.minecraft)
	implementation(libs.bundles.mc.libs)
	implementation(libs.jspecify)
	implementation(libs.skyblocker)

	testImplementation(platform("org.junit:junit-bom:6.0.0"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
	mainClass = "me.alex.workflow.Main"
}

tasks.test {
	useJUnitPlatform()
}

package app.morphe.extension.spotify.shared;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import app.morphe.extension.shared.Logger;
import app.morphe.extension.shared.ResourceType;
import app.morphe.extension.shared.ResourceUtils;

public final class ComponentFilters {

    public interface ComponentFilter {
        @NonNull
        String getFilterValue();

        String getFilterRepresentation();

        default boolean filterUnavailable() {
            return false;
        }
    }

    public static final class ResourceIdComponentFilter implements ComponentFilter {

        public final ResourceType resourceType;
        public final String resourceName;
        // Android resources are always positive, so -1 is a valid sentinel value to indicate it has not been loaded.
        // 0 is returned when a resource has not been found.
        private int resourceId = -1;
        @Nullable
        private String stringfiedResourceId;

        public ResourceIdComponentFilter(ResourceType resourceType, String resourceName) {
            this.resourceType = resourceType;
            this.resourceName = resourceName;
        }

        public int getResourceId() {
            if (resourceId == -1) {
                resourceId = ResourceUtils.getIdentifier(resourceType, resourceName);
            }
            return resourceId;
        }

        @NonNull
        @Override
        public String getFilterValue() {
            if (stringfiedResourceId == null) {
                stringfiedResourceId = Integer.toString(getResourceId());
            }
            return stringfiedResourceId;
        }

        @Override
        public String getFilterRepresentation() {
            boolean resourceFound = getResourceId() != 0;
            return (resourceFound ? getFilterValue() + " (" : "") + resourceName + (resourceFound ? ")" : "");
        }

        @Override
        public boolean filterUnavailable() {
            boolean resourceNotFound = getResourceId() == 0;
            if (resourceNotFound) {
                Logger.printInfo(() -> "Resource id for " + resourceName + " was not found");
            }
            return resourceNotFound;
        }
    }

    public record StringComponentFilter(String string) implements ComponentFilter {

        @NonNull
            @Override
            public String getFilterValue() {
                return string;
            }

            @Override
            public String getFilterRepresentation() {
                return string;
            }
        }
}

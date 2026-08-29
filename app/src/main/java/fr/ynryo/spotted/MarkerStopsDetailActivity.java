package fr.ynryo.spotted;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PictureDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.color.MaterialColors;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import fr.ynryo.spotted.apiResponsesPOJO.guessPlatform.CartoTchooGuessPlatform;
import fr.ynryo.spotted.apiResponsesPOJO.network.BusTrackerNetworkData;
import fr.ynryo.spotted.artists.MarkerArtist;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStandardized;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStop;
import fr.ynryo.spotted.genericMarkerDatas.MarkerStopPlatform;
import fr.ynryo.spotted.managers.FetchingManager;
import fr.ynryo.spotted.utils.Time;

public class MarkerStopsDetailActivity {
    private static final String TAG = "MarkerStopsDetailActivity";
    private static final int COLOR_GREEN = Color.rgb(15, 150, 40);
    private final MainActivity context;
    private View bottomSheetView;
    private BottomSheetBehavior<View> behavior;
    private String vehicleId;

    public MarkerStopsDetailActivity(MainActivity context) {
        WeakReference<MainActivity> contextRef = new WeakReference<>(context);
        this.context = contextRef.get();
        initBottomSheet();
    }

    private void initBottomSheet() {
        if (this.context == null) return;
        bottomSheetView = context.findViewById(R.id.vehicle_bottom_sheet);
        if (bottomSheetView != null) {
            behavior = BottomSheetBehavior.from(bottomSheetView);
            behavior.setHideable(true);
            behavior.setFitToContents(false);
            behavior.setExpandedOffset(context.dpToPx(84));
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            behavior.setPeekHeight(calculatePeekHeight());

            behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        vehicleId = null;
                        if (context != null && context.getMarkerArtist() != null && context.getMarkerArtist().getRouteArtist() != null) {
                            context.getMarkerArtist().getRouteArtist().remove();
                        }
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });

            View closeBtn = bottomSheetView.findViewById(R.id.closeButton);
            if (closeBtn != null) {
                closeBtn.setOnClickListener(v -> close());
            }

            ViewCompat.setOnApplyWindowInsetsListener(bottomSheetView, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                View nsvContent = bottomSheetView.findViewById(R.id.nsvContent);
                if (nsvContent != null) {
                    nsvContent.setPadding(nsvContent.getPaddingLeft(), nsvContent.getPaddingTop(), nsvContent.getPaddingRight(), insets.bottom + context.dpToPx(16));
                    if (nsvContent instanceof NestedScrollView) {
                        ((NestedScrollView) nsvContent).setClipToPadding(false);
                    }
                }
                return windowInsets;
            });
        }
    }

    public void open(MarkerStandardized markerStandardized) {
        if (this.context == null) return;
        if (bottomSheetView == null || behavior == null) initBottomSheet();
        if (bottomSheetView == null || behavior == null) return;

        vehicleId = markerStandardized.getId();

        setupLineHeader(bottomSheetView, markerStandardized);
        setupLoader(bottomSheetView, markerStandardized);

        behavior.setPeekHeight(calculatePeekHeight());
        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        context.findViewById(R.id.btn_open_menu).setVisibility(View.GONE);
        context.findViewById(R.id.compass).setVisibility(View.GONE);
        context.findViewById(R.id.fab_center_location).setVisibility(View.GONE);
        context.findViewById(R.id.changeMapStyle).setVisibility(View.GONE);

        fetchVehicleData(markerStandardized, bottomSheetView);
    }

    public void close() {
        if (behavior != null) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            context.findViewById(R.id.btn_open_menu).setVisibility(View.VISIBLE);
            context.findViewById(R.id.compass).setVisibility(View.VISIBLE);
            context.findViewById(R.id.fab_center_location).setVisibility(View.VISIBLE);
            context.findViewById(R.id.changeMapStyle).setVisibility(View.VISIBLE);
        }
        vehicleId = null;

        MarkerArtist markerArtist = context.getMarkerArtist();
        if (markerArtist != null && markerArtist.getRouteArtist() != null) {
            markerArtist.getRouteArtist().remove();
        }
    }

    private int calculatePeekHeight() {
        if (context == null) return 400;
        return context.dpToPx(130);
    }

    private void setupLineHeader(View view, MarkerStandardized markerStandardized) {
        TextView tvLigne = view.findViewById(R.id.tvLigneNumero);

        String lineNumber = markerStandardized.getLineNumber();
        tvLigne.setText(lineNumber);

        int fillColor = Color.parseColor(markerStandardized.getFillColor() != null ? markerStandardized.getFillColor() : "#424242");
        int textColor = Color.parseColor(markerStandardized.getTextColor() != null ? markerStandardized.getTextColor() : "#FFFFFF");

        tvLigne.setBackgroundColor(fillColor);
        tvLigne.setTextColor(textColor);
    }

    private void setupLoader(View view, MarkerStandardized markerStandardized) {
        ProgressBar loader = view.findViewById(R.id.loader);
        int fillColor = Color.parseColor(markerStandardized.getFillColor() != null ? markerStandardized.getFillColor() : "#424242");

        loader.setVisibility(View.VISIBLE);
        loader.setIndeterminateTintList(ColorStateList.valueOf(fillColor));

        view.findViewById(R.id.llStopsContent).setVisibility(View.INVISIBLE);
    }

    // ==================== DATA FETCHING ====================

    /**
     * Fetch data from API
     *
     * @param markerStandardized the marker data
     * @param view               the view
     */
    private void fetchVehicleData(MarkerStandardized markerStandardized, View view) {
        context.getFetcher().fetchVehicleStopsInfo(markerStandardized, new FetchingManager.OnVehicleDetailsListener() {
            @Override
            public void onResponseVehicleDetailsListener(MarkerStandardized markerStandardized) {
                hideLoader(view);

                if (context.getMarkerArtist() != null) {
                    context.getMarkerArtist().getRouteArtist().drawVehicleRoute(markerStandardized);
                }

                showVehicleDetails(markerStandardized, view);
                fetchNetworkLogo(markerStandardized, view);
            }

            @Override
            public void onErrorVehicleDetailsListener(String error) {
                hideLoader(view);
                showError(view);
            }
        });
    }

    /**
     * Fetch network logo from API
     *
     * @param markerStandardized the marker data
     * @param view               the view
     */
    private void fetchNetworkLogo(MarkerStandardized markerStandardized, View view) {
        if (markerStandardized.getNetworkId() == 0) return;

        context.getFetcher().fetchNetworkData(markerStandardized.getNetworkId(), new FetchingManager.OnNetworkDataListener() {
            @Override
            public void onResponseNetworkDataListener(BusTrackerNetworkData nData) {
                loadNetworkLogo(view, nData.getLogoHref());
            }

            @Override
            public void onErrorNetworkDataListener(String error) {
                Log.w(TAG, "Erreur lors de la récuperation du logo");
            }
        });
    }

    /**
     * Load network logo from API
     *
     * @param view   the view
     * @param imgURI the URI of the logo
     */
    private void loadNetworkLogo(View view, URI imgURI) {
        ImageView ivLogo = view.findViewById(R.id.ivNetworkLogo);
        if (imgURI == null) {
            ivLogo.setVisibility(View.GONE);
            return;
        }

        ivLogo.setVisibility(View.VISIBLE);
        ivLogo.setBackgroundResource(R.color.surface_light);
        ivLogo.setAdjustViewBounds(true);
        ivLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Glide.with(context).as(PictureDrawable.class).load(imgURI.toString()).diskCacheStrategy(DiskCacheStrategy.DATA).override(100, 100).into(ivLogo);
    }

    /**
     * Hide loader from view
     *
     * @param view the view
     */
    private void hideLoader(View view) {
        view.findViewById(R.id.loader).setVisibility(View.GONE);
    }

    /**
     * Show error from view
     *
     * @param view the view
     */
    private void showError(View view) {
        TextView tvDest = view.findViewById(R.id.tvDestination);
        tvDest.setText(R.string.network_error);
    }

    // ==================== DISPLAY ====================

    /**
     * Show vehicle details from marker data
     *
     * @param markerStandardized the marker data
     * @param view               the view
     */
    private void showVehicleDetails(MarkerStandardized markerStandardized, View view) {
        context.getFollowManager().setFollowButton(view.findViewById(R.id.followButton), markerStandardized.getId());
        context.getFavoriteManager().setFavoriteButton(view.findViewById(R.id.favoriteButton), markerStandardized);

        setupDestinationText(view, markerStandardized);
        StopsAdapter adapter = setupStopsList(view, markerStandardized);
        fetchGuessPlatforms(markerStandardized, adapter);
    }

    /**
     * Setup destination text from marker data
     *
     * @param view               the view
     * @param markerStandardized the marker data
     */
    private void setupDestinationText(View view, MarkerStandardized markerStandardized) {
        TextView tvDestination = view.findViewById(R.id.tvDestination);
        tvDestination.setText(markerStandardized.getDestination());
        tvDestination.setSingleLine(true);
        tvDestination.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvDestination.setMarqueeRepeatLimit(-1);
        tvDestination.setHorizontallyScrolling(true);
        tvDestination.setSelected(true);
    }

    private StopsAdapter setupStopsList(View view, MarkerStandardized markerStandardized) {
        RecyclerView rvStops = view.findViewById(R.id.rvStops);
        rvStops.setLayoutManager(new LinearLayoutManager(context));

        List<MarkerStop> stops = markerStandardized.getStops() != null ? markerStandardized.getStops() : new ArrayList<>();
        StopsAdapter adapter = new StopsAdapter(stops);
        rvStops.setAdapter(adapter);

        view.findViewById(R.id.llStopsContent).setVisibility(View.VISIBLE);
        return adapter;
    }

    private void fetchGuessPlatforms(MarkerStandardized markerStandardized, StopsAdapter adapter) {
        if (!markerStandardized.isTrain()) return;

        String trainNum = markerStandardized.getLineNumber();
        if (trainNum == null || trainNum.isEmpty()) return;

        List<MarkerStop> stops = markerStandardized.getStops();
        for (int i = 0; i < stops.size(); i++) {
            MarkerStop stop = stops.get(i);
            // Si l'arrêt a déjà un quai officiel renseigné (100%), on ne fait pas de requête CartoTchoo
            if (stop.getPlatform() != null && stop.getPlatform().getPlatformName() != null && !stop.getPlatform().getPlatformName().isEmpty()) {
                continue;
            }
            final int position = i;
            final String uic = stop.getStopRef();
            if (uic == null || uic.isEmpty()) continue;

            Log.d(TAG, "Fetching guess platform: uic=" + uic + ", trainNum=" + trainNum);
            context.getFetcher().fetchGuestPlatform(uic, trainNum, new FetchingManager.OnGuessPlatformListener() {
                @Override
                public void onResponseGuessPlatformListener(List<CartoTchooGuessPlatform> cartoTchooGuessPlatform) {
                    Log.d(TAG, "Réponse guess platform pour UIC " + uic + ": " + cartoTchooGuessPlatform);
                    if (cartoTchooGuessPlatform != null && !cartoTchooGuessPlatform.isEmpty()) {
                        markerStandardized.setGuessStopPlatform(uic, cartoTchooGuessPlatform);
                        if (adapter != null) {
                            adapter.notifyItemChanged(position);
                        }
                    }
                }

                @Override
                public void onErrorGuessPlatformListener(String error) {
                    Log.d(TAG, "Pas d'estimation de quai pour UIC " + uic + " : " + error);
                }
            });
        }
    }

    public String getCurrentVehicleId() {
        return vehicleId;
    }

    private static int getTimelineLayout(MarkerStop stop, int position, int itemCount) {
        boolean isFirstStop = stop.isDepartureStop();
        boolean isLastStop = position == itemCount - 1 || stop.isDestinationStop();
        if (isFirstStop) {
            return R.layout.timeline_first_stop;
        } else if (isLastStop) {
            return R.layout.timeline_last_stop;
        } else {
            return R.layout.timeline_intermediate_stop;
        }
    }

    // ==================== ADAPTER ====================

    private static class StopViewHolder extends RecyclerView.ViewHolder {
        final View sllPlatformContainer;
        final TextView tvPlatform, tvPlatformLabel, tvStopName, tvDepartureTime, tvScheduledDepartureTime, tvAtStopTime, tvArrivingTime, tvScheduledArrivingTime, tvDelay;
        final ImageView ivArrivingTimeIcon, ivDepartureTimeIcon;
        final ViewFlipper vfTime;
        final FrameLayout flTimeline;

        StopViewHolder(View itemView) {
            super(itemView);
            sllPlatformContainer = itemView.findViewById(R.id.llPlatformContainer);
            tvPlatform = itemView.findViewById(R.id.tvPlatform);
            tvPlatformLabel = itemView.findViewById(R.id.tvPlatformLabel);
            tvStopName = itemView.findViewById(R.id.tvStopName);
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvScheduledDepartureTime = itemView.findViewById(R.id.tvScheduledDepartureTime);
            tvAtStopTime = itemView.findViewById(R.id.tvAtStopTime);
            tvArrivingTime = itemView.findViewById(R.id.tvArrivingTime);
            tvScheduledArrivingTime = itemView.findViewById(R.id.tvScheduledArrivingTime);
            tvDelay = itemView.findViewById(R.id.tvDelay);
            ivArrivingTimeIcon = itemView.findViewById(R.id.ivArrivingTimeIcon);
            ivDepartureTimeIcon = itemView.findViewById(R.id.ivDepartureTimeIcon);
            vfTime = itemView.findViewById(R.id.llTrainAData);
            flTimeline = itemView.findViewById(R.id.flTimeline);
        }
    }

    /**
     * Stops adapter for recycler view
     */
    private class StopsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_STOP = 0;
        private static final int TYPE_EMPTY = 1;

        private final List<MarkerStop> stops;

        /**
         * Constructor
         *
         * @param stops the list of stops
         */
        StopsAdapter(List<MarkerStop> stops) {
            this.stops = stops;
        }

        /**
         * Get item view type
         *
         * @param position position to query
         * @return the item view type
         */
        @Override
        public int getItemViewType(int position) {
            return stops.isEmpty() ? TYPE_EMPTY : TYPE_STOP;
        }

        /**
         * Create a view holder
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         */
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_EMPTY) {
                return createEmptyViewHolder(parent);
            }
            return createStopViewHolder(parent);
        }

        /**
         * Bind view holder
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_EMPTY) {
                bindEmptyViewHolder(holder);
                return;
            }

            MarkerStop stop = stops.get(position);
            bindStopViewHolder((StopViewHolder) holder, stop, position, stops.size());
        }

        /**
         * Get item count
         *
         * @return the item count
         */
        @Override
        public int getItemCount() {
            return stops.isEmpty() ? 1 : stops.size();
        }

        // ========== NO DATA ==========

        /**
         * Create empty view holder
         *
         * @param parent The ViewGroup into which the new View will be added after it is bound to
         *               an adapter position.
         * @return A new ViewHolder that holds a View of the given view type.
         */
        private RecyclerView.ViewHolder createEmptyViewHolder(ViewGroup parent) {
            TextView tvEmpty = new TextView(parent.getContext());
            tvEmpty.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvEmpty.setPadding(0, 32, 0, 32);
            tvEmpty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            return new RecyclerView.ViewHolder(tvEmpty) {
            };
        }

        /**
         * Bind empty view holder
         *
         * @param holder The ViewHolder which should be updated to represent the contents of the
         *               item at the given position in the data set.
         */
        private void bindEmptyViewHolder(RecyclerView.ViewHolder holder) {
            TextView tvEmpty = (TextView) holder.itemView;
            tvEmpty.setText(R.string.no_data);
            tvEmpty.setTextColor(MaterialColors.getColor(holder.itemView, com.google.android.material.R.attr.colorOnSurface));
        }

        // ========== STOP ITEM ==========
        private RecyclerView.ViewHolder createStopViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_stop_details, parent, false);
            return new StopViewHolder(view);
        } //inflate item stop

        private void bindStopViewHolder(StopViewHolder vh, MarkerStop stop, int position, int itemCount) { //distribute data
            bindTimeline(vh, stop, position, itemCount);
            bindPlatform(vh, stop);
            bindStopName(vh, stop);
            bindArrivalTime(vh, stop);
            bindAtStopTime(vh, stop);
            bindDepartureTime(vh, stop);
            bindDelay(vh, stop);
        }

        public void bindTimeline(StopViewHolder vh, MarkerStop stop, int position, int itemCount) {
            MarkerStandardized vehicle = stop.getVehicle();

            vh.flTimeline.setVisibility(View.VISIBLE);
            vh.flTimeline.removeAllViews();

            // Inflate le layout dedans
            View timelineView = LayoutInflater.from(context).inflate(getTimelineLayout(stop, position, itemCount), vh.flTimeline, true);

            // Tinte la barre avec la couleur du train
            int fillColor = Color.parseColor(vehicle.getFillColor() != null ? vehicle.getFillColor() : "#424242");

            View lineView = timelineView.findViewById(R.id.vLineBottom);
            if (lineView == null) lineView = timelineView.findViewById(R.id.vLineTop);
            if (lineView == null) lineView = timelineView.findViewById(R.id.vLineFull);
            if (lineView != null)
                ((GradientDrawable) lineView.getBackground().mutate()).setColor(fillColor);
        }

        private void bindPlatform(StopViewHolder vh, MarkerStop stop) {
            MarkerStopPlatform platform = stop.getPlatform();
            if (platform != null && platform.getPlatformName() != null) {
                vh.tvPlatform.setText(platform.getPlatformName());
                vh.sllPlatformContainer.setVisibility(View.VISIBLE);

                GradientDrawable gd = (GradientDrawable) vh.sllPlatformContainer.getBackground().mutate();
                boolean isPlatformGuessed = platform.getPercentage() != 100;
                if (isPlatformGuessed) {
                    vh.tvPlatform.setTextColor(Color.GRAY);
                    vh.tvPlatformLabel.setTextColor(Color.GRAY);
                    gd.setStroke(2, Color.GRAY, 8, 8);
                }
            } else {
                vh.sllPlatformContainer.setVisibility(View.GONE);
            }
        }

        private void bindStopName(StopViewHolder vh, MarkerStop stop) {
            SpannableStringBuilder builder = new SpannableStringBuilder(stop.getStopName());

            int iconRes = getStopIconResource(stop);
            if (iconRes != 0) {
                appendStopIcon(vh, builder, iconRes);
            }

            vh.tvStopName.setText(builder);
            vh.tvStopName.setSelected(true);
        }

        private int getStopIconResource(MarkerStop stop) {
            if (stop.cantPickup()) return R.drawable.icon_logout;
            if (stop.cantDropoff()) return R.drawable.icon_login;
            return 0;
        }

        private void appendStopIcon(StopViewHolder vh, SpannableStringBuilder builder, int iconRes) {
            builder.append("  ");
            Drawable d = ContextCompat.getDrawable(context, iconRes);
            if (d != null) {
                d.mutate();
                d.setColorFilter(new PorterDuffColorFilter(MaterialColors.getColor(vh.tvStopName, com.google.android.material.R.attr.colorOnSurface), PorterDuff.Mode.SRC_IN));
                int size = (int) (vh.tvStopName.getTextSize() * 1.2f);
                d.setBounds(0, 0, size, size);
                builder.setSpan(new ImageSpan(d, ImageSpan.ALIGN_BOTTOM), builder.length() - 1, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        private void bindArrivalTime(StopViewHolder vh, MarkerStop stop) {
            MarkerStandardized vehicle = stop.getVehicle();
            if (stop.isDepartureStop()) {
                vh.tvArrivingTime.setVisibility(View.GONE);
                vh.tvScheduledArrivingTime.setVisibility(View.GONE);
                vh.ivArrivingTimeIcon.setVisibility(View.GONE);
                return;
            }

            Time arrivalTime = stop.getArrivalTime() != null ? stop.getArrivalTime() : (stop.isDestinationStop() ? stop.getDepartureTime() : null);
            if (arrivalTime != null && (vehicle.isTrain() || stop.isDestinationStop())) {
                vh.tvArrivingTime.setVisibility(View.VISIBLE);
                vh.tvArrivingTime.setText(Time.formatHHmm(arrivalTime));
                vh.tvArrivingTime.setTextColor(stop.isOnLive() ? COLOR_GREEN : getDefaultTextColor(vh));
                vh.ivArrivingTimeIcon.setVisibility(View.VISIBLE);
                bindOnLive(vh.ivArrivingTimeIcon, stop);

                if (stop.getDelay() != null && stop.getDelay() != 0) {
                    Time scheduledTime = stop.isLate() ? Time.calculateTimeWithoutDelay(arrivalTime, stop.getDelay()) : Time.calculateTimeWithoutAdvance(arrivalTime, stop.getDelay());
                    vh.tvScheduledArrivingTime.setVisibility(View.VISIBLE);
                    vh.tvScheduledArrivingTime.setText(Time.formatHHmm(scheduledTime));
                    vh.tvScheduledArrivingTime.setPaintFlags(vh.tvScheduledArrivingTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    vh.tvScheduledArrivingTime.setTextColor(stop.getDelayColor());
                } else {
                    vh.tvScheduledArrivingTime.setVisibility(View.GONE);
                }
            } else {
                vh.tvArrivingTime.setVisibility(View.GONE);
                vh.tvScheduledArrivingTime.setVisibility(View.GONE);
                vh.ivArrivingTimeIcon.setVisibility(View.GONE);
            }
        }

        private void bindAtStopTime(StopViewHolder vh, MarkerStop stop) {
            MarkerStandardized vehicle = stop.getVehicle();
            if (!vehicle.isTrain() || stop.isDestinationStop() || stop.isDepartureStop()) {
                vh.tvAtStopTime.setVisibility(View.GONE);
                return;
            }
            Long atStopMinutes = stop.getAtStopTime();
            if (atStopMinutes != null && atStopMinutes >= 0) {
                vh.tvAtStopTime.setVisibility(View.VISIBLE);
                vh.tvAtStopTime.setText(atStopMinutes + "min d'arrêt");
                vh.tvAtStopTime.setTextColor(Color.GRAY);
            } else {
                vh.tvAtStopTime.setVisibility(View.GONE);
            }
        }

        private void bindDepartureTime(StopViewHolder vh, MarkerStop stop) {
            if (stop.isDestinationStop()) {
                vh.tvDepartureTime.setVisibility(View.GONE);
                vh.tvScheduledDepartureTime.setVisibility(View.GONE);
                vh.ivDepartureTimeIcon.setVisibility(View.GONE);
                return;
            }

            Time departureTime = stop.getDepartureTime();
            if (departureTime != null) {
                vh.tvDepartureTime.setVisibility(View.VISIBLE);
                vh.tvDepartureTime.setText(Time.formatHHmm(departureTime));
                vh.tvDepartureTime.setTextColor(stop.isOnLive() ? COLOR_GREEN : getDefaultTextColor(vh));
                bindOnLive(vh.ivDepartureTimeIcon, stop);

                if (stop.getDelay() != null && stop.getDelay() != 0) {
                    Time scheduledTime = stop.isLate() ? Time.calculateTimeWithoutDelay(departureTime, stop.getDelay()) : Time.calculateTimeWithoutAdvance(departureTime, stop.getDelay());
                    vh.tvScheduledDepartureTime.setVisibility(View.VISIBLE);
                    vh.tvScheduledDepartureTime.setText(Time.formatHHmm(scheduledTime));
                    vh.tvScheduledDepartureTime.setPaintFlags(vh.tvScheduledDepartureTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    vh.tvScheduledDepartureTime.setTextColor(stop.getDelayColor());
                } else {
                    vh.tvScheduledDepartureTime.setVisibility(View.GONE);
                }
            } else {
                vh.tvDepartureTime.setText("??:??");
                vh.tvScheduledDepartureTime.setVisibility(View.GONE);
                vh.ivDepartureTimeIcon.setVisibility(View.GONE);
            }
        }

        private void bindOnLive(ImageView ivTimeIcon, MarkerStop stop) {
            if (stop.isOnLive()) {
                ivTimeIcon.setImageResource(R.drawable.icon_sensors);
                ivTimeIcon.setColorFilter(COLOR_GREEN);
            } else {
                ivTimeIcon.setImageResource(R.drawable.icon_no_sensors);
            }
        }

        private void bindDelay(StopViewHolder vh, MarkerStop stop) {
            if (vh.vfTime != null) {
                vh.vfTime.stopFlipping();
                vh.vfTime.setDisplayedChild(0);
            }
            if (stop.getDelay() == null || stop.getDelay() == 0) {
                vh.tvDelay.setVisibility(View.GONE);
                return;
            }

            vh.tvDelay.setVisibility(View.VISIBLE);
            vh.tvDelay.setText(stop.getDelayStatusText());
            vh.tvDelay.setPaintFlags(vh.tvDelay.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            vh.tvDelay.setTextColor(stop.getDelayColor());
            if (vh.vfTime != null) {
                vh.vfTime.startFlipping();
            }
        }

        private int getDefaultTextColor(StopViewHolder vh) {
            return MaterialColors.getColor(vh.tvDepartureTime, com.google.android.material.R.attr.colorOnSurface);
        }
    }
}
package com.itachi1706.droideggs.NougatEgg.EasterEgg.neko;

/**
 * Created by Kenneth on 8/9/2016.
 * for com.itachi1706.droideggs.NougatEgg.EasterEgg.neko in DroidEggs
 */
public class NekoDialog extends Dialog {
    private final Adapter mAdapter;
    public NekoDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Material_Dialog_NoActionBar);
        RecyclerView view = new RecyclerView(getContext());
        mAdapter = new Adapter(getContext());
        view.setLayoutManager(new GridLayoutManager(getContext(), 2));
        view.setAdapter(mAdapter);
        final float dp = context.getResources().getDisplayMetrics().density;
        final int pad = (int)(16*dp);
        view.setPadding(pad, pad, pad, pad);
        setContentView(view);
    }
    private void onFoodSelected(Food food) {
        PrefState prefs = new PrefState(getContext());
        int currentState = prefs.getFoodState();
        if (currentState == 0 && food.getType() != 0) {
            NekoService.registerJob(getContext(), food.getInterval(getContext()));
        }
        prefs.setFoodState(food.getType());
        dismiss();
    }
    private class Adapter extends RecyclerView.Adapter<Holder> {
        private final Context mContext;
        private final ArrayList<Food> mFoods = new ArrayList<>();
        public Adapter(Context context) {
            mContext = context;
            int[] foods = context.getResources().getIntArray(R.array.food_names);
            // skip food 0, you can't choose it
            for (int i=1; i<foods.length; i++) {
                mFoods.add(new Food(i));
            }
        }
        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.food_layout, parent, false));
        }
        @Override
        public void onBindViewHolder(final Holder holder, int position) {
            final Food food = mFoods.get(position);
            ((ImageView) holder.itemView.findViewById(R.id.icon))
                    .setImageIcon(food.getIcon(mContext));
            ((TextView) holder.itemView.findViewById(R.id.text))
                    .setText(food.getName(mContext));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFoodSelected(mFoods.get(holder.getAdapterPosition()));
                }
            });
        }
        @Override
        public int getItemCount() {
            return mFoods.size();
        }
    }
    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }
}
package submission.learning.storyapp.interfaces.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import submission.learning.storyapp.R
import submission.learning.storyapp.data.response.ListStoryItem
import submission.learning.storyapp.databinding.ItemStoryBinding
import submission.learning.storyapp.interfaces.detail.DetailActivity


class ListUserAdapter(private val listStories: List<ListStoryItem>): RecyclerView.Adapter<ListUserAdapter.ListViewHolder>() {
    inner class ListViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        private var idStoryImage: ImageView = itemView.findViewById(R.id.id_story_image)
        private var idText: TextView = itemView.findViewById(R.id.id_name)
        fun bind(listStoryItem: ListStoryItem) {
            binding.idName.text = listStoryItem.name

            Glide
                .with(itemView.context)
                .load(listStoryItem.photoUrl)
                .fitCenter()
                .into(binding.idStoryImage)

            binding.itemStory.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("listItemStory", listStoryItem)

                val options: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(idStoryImage, "photo"),
                        Pair(idText, "name")
                    )
                itemView.context.startActivity(intent, options.toBundle())
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listStories.size
    }

    override fun onBindViewHolder(holder: ListUserAdapter.ListViewHolder, position: Int) {
        val item = listStories[position]
        holder.bind(item)
    }

}
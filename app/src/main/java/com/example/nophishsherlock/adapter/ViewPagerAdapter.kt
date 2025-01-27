import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Der ViewPagerAdapter ist ein Adapter für die ViewPager2.
 *
 * @property fragmentList Eine Liste von Fragmenten, die angezeigt werden sollen.
 * @constructor Erstellt ein neues ViewPagerAdapter mit den angegebenen Fragmenten.
 *
 * @param fragmentActivity Die Activity, in der das Adapter verwendet wird.
 */
class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val fragmentList: MutableList<Fragment>,
) :
    FragmentStateAdapter(fragmentActivity) {


    // 1. getItemCount(): Anzahl der anzuzeigenden Fragmente
    override fun getItemCount(): Int = fragmentList.size

    // 2. createFragment(position: Int): Erstellt das Fragment für die gegebene Position
    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }


}
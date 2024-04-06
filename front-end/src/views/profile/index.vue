<template>
  <div class="app-container">
    <div v-if="user">
      <el-row :gutter="20">

        <div>
          <user-card :user="user" />
        </div>

        <!-- <el-col :span="18" :xs="24">
          <el-card>
            <el-tabs v-model="activeTab">
              <el-tab-pane label="Activity" name="activity">
                <activity />
              </el-tab-pane>
              <el-tab-pane label="Timeline" name="timeline">
                <timeline />
              </el-tab-pane>
              <el-tab-pane label="Account" name="account">
                <account :user="user" />
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col> -->

      </el-row>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import UserCard from './components/UserCard'

export default {
  name: 'Profile',
  components: { UserCard },
  data() {
    return {
      user: {},
      activeTab: 'activity'
    }
  },
  computed: {
    ...mapGetters([
      'name',
      'avatar',
      'roles'
    ])
  },
  created() {
    this.getUser()
  },
  methods: {
    getUser() {
      this.user = {
        name: this.name,
        role: this.roles.join(' | '),
        email: 'admin@test.com',
        avatar: this.avatar

        // username: this.username,
        // address: this.address,
        // email: this.email,
        // avatar: this.avatar,
        // role: this.role,
        // birthday: this.birthday,
        // fullName: this.fullName,
        // gender: this.gender,
        // phoneNumber: this.phoneNumber
      }
    }
  }
}
</script>

<template>
  <div>
    <el-calendar
      ref="calendar"
      :key="calendarKey"
    >

      <template
        slot="dateCell"
        slot-scope="{date, data}"
      >
        <div
          class="current-click"
          style="index: -1"
          @click="handleCreate(data)"
        >
          <p style="margin: 2px">{{ data.day.split('-').slice(2).join('-') }}</p>
          <div
            v-for="(item, ind) in leaveList"
            :key="'patrolScheduleInd' + ind"
          >
            <div v-if="handleCheckDate(item.time, data.day)">
              <div class="current" @click="labelPatrolSchedule($event, item, data)">
                <el-tooltip
                  :content="getStatusName(item.type)"
                  placement="top-start"
                  class="box-item"
                  effect="dark"
                >
                  <span class="current-content">
                    {{ formattedTitle(getStatusName(item.type)) }} - {{ formatDate(item.startTime) }} - {{ formatDate(item.endTime) }}
                  </span>
                </el-tooltip>
              </div>
            </div>
          </div>
        </div>
      </template>
    </el-calendar>
    <Form v-if="dialogVisible" :detail-leave="detailLeave" :dialog-visible="dialogVisible" @close="handleClose" @success="handleSuccess" />
  </div>
</template>
<script>
import { inject } from 'vue'
import Cookies from 'js-cookie'
import axios from 'axios'
import moment from 'moment'
import Form from './form.vue'
export default {
  name: 'LeaveUser',
  components: { Form },
  setup() {
    const appName = inject('appName')
    console.log(appName)
  },
  data() {
    return {
      loading: false,
      leaveList: [],
      dialogVisible: false,
      detailLeave: null,
      calendarKey: '',
      timeSearch: moment(),
      calendar: null,
      leaveStatus: [
        { value: 'LEAVE', label: 'Nghỉ phép' },
        { value: 'SICK', label: 'Nghỉ ốm' },
        { value: 'ABSENT', label: 'Vắng mặt' }
      ]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const headers = {
        'Content-Type': 'multipart/form-data',
        Authorization: 'Bearer ' + Cookies.get('access-token')
      }
      axios
        .get(process.env.VUE_APP_API + 'management/leave', {
          headers: headers,
          params: {
            page: 0,
            size: 100,
            startTime: '2024-01-01 00:00:00',
            endTime: '2025-01-01 00:00:00'
          }
        })
        .then((res) => {
          if (res.data) {
            this.leaveList = res.data.data
            this.loading = false
          }
        })
        .catch((err) => {
          this.loading = false
          this.$message({
            message: err.response.data.message,
            type: 'error'
          })
        })
    },
    handleClose() {
      this.detailLeave = null
      this.dialogVisible = false
    },
    handleSuccess() {
      this.detailLeave = null
      this.dialogVisible = false
      this.getList()
    },
    handleCreate(data) {
      if (data) {
        const dateSelect = moment(data.day).unix()
        const dateNow = moment().clone().format('YYYY-MM-DD 00:00:00')
        const dateCurrent = moment(dateNow).unix()
        if (dateSelect >= dateCurrent) {
          this.dialogVisible = true
        } else {
          this.$message.warning('Không thể thêm lịch tuần tra trong quá khứ!')
        }
      }
    },
    getStatusName(status) {
      const result = this.leaveStatus.find(i => {
        return i.value === status
      })
      return result?.label
    },
    selectDate(action) {
      if (action === 'today') {
        this.timeSearch = new Date()
      } else if (action === 'next-month') {
        this.timeSearch = moment(this.timeSearch).add(1, 'month').toDate()
      } else if (action === 'prev-month') {
        this.timeSearch = moment(this.timeSearch).subtract(1, 'month').toDate()
      }

      if (this.calendar) {
        this.calendar.selectDate(action)
      }
      this.getList()
    },
    handleCheckDate(date1, date2) {
      const dateOne = moment(date1)
      const dateTrue = moment(date2)
      return dateOne.isSame(dateTrue)
    },

    labelPatrolSchedule($event, item, data) {
      this.detailLeave = item
    },

    formattedTitle(val) {
      if (val) {
        const maxLength = 11
        return val.length > maxLength ? val.slice(0, maxLength) + '...' : val
      } else {
        return ''
      }
    },
    formatDate(val) {
      return moment(val).format('HH:mm DD/MM/YYYY')
    }
  }
}
</script>
<style lang="scss">
.current-content {
  font-size: 13px;
  color: #fff;
  word-break: break-word;
  display: block;
  padding: 4px;
  border-radius: 4px;
  margin-block: 5px;
  background-color: #3a87ad;
}
.current-click {
  margin: 2px;
  min-height: 120px;
  height: 100%;
}
</style>

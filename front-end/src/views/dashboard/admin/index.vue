<!-- eslint-disable vue/no-async-in-computed-properties -->
<template>
  <div class="dashboard-container">
    <div class="db-header">
      <div class="dashboard-title">Tổng quan</div>
      <div class="db-header-filter">
        <div>
          <el-select v-model="departmentId" placeholder="Tất cả đơn vị">
            <el-option
              v-for="item in departmentList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </div>
        <div class="filter-config">
          <i class="el-icon-s-tools" />
          Tùy chỉnh
        </div>
      </div>
    </div>
    <div class="db-body">
      <div class="db-left">
        <div class="db-left-item">
          <div class="db-left-item-title">Đi muộn, về sớm</div>
          <el-select v-model="time1" placeholder="Select">
            <el-option
              v-for="item in optionTimes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="db-left-item-number">
            <div>0</div>
          </div>
          <div class="db-left-item-detail">
            Chi tiết
          </div>
        </div>
        <div class="db-left-item">
          <div class="db-left-item-title">Thực tế đã nghỉ</div>
          <el-select v-model="time2" placeholder="Select">
            <el-option
              v-for="item in optionTimes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="db-left-item-number">
            <div>0</div>
          </div>
          <div class="db-left-item-detail">
            Chi tiết
          </div>
        </div>
        <div class="db-left-item">
          <div class="db-left-item-title">Kế hoạch nghỉ</div>
          <el-select v-model="time3" placeholder="Select">
            <el-option
              v-for="item in optionTimes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <div class="db-left-item-number">
            <div>0</div>
          </div>
          <div class="db-left-item-detail">
            Chi tiết
          </div>
        </div>
      </div>
      <div class="db-right">
        <div class="db-right-top">
          <div class="db-right-top-item">
            <div class="leave-time-header">
              <div>
                <div class="leave-time-header-title">Tình hình nghỉ theo thời gian</div>
                <div class="leave-time-header-content">Tất cả đơn vị</div>
                <div class="leave-time-header-content">(01/11/2021 - 05/12/2021)</div>
              </div>
              <div class="leave-time-icon">
                <i class="el-icon-refresh-left" />
                <i class="el-icon-download" />
                <i class="el-icon-setting" />
              </div>
            </div>
            <LineChart />
          </div>
          <div class="db-right-top-item">
            <div class="leave-time-header">
              <div>
                <div class="leave-time-header-title">Tình hình nghỉ theo phòng ban</div>
                <div class="leave-time-header-content-2">Công ty cổ phần công nghệ viễn thông Elcom</div>
                <div class="leave-time-header-content-2">(01/11/2021 - 05/12/2021)</div>
              </div>
              <div class="leave-time-icon" />
            </div>
            <ColumnChart />
          </div>
        </div>
        <div class="db-right-bottom">
          <div class="db-right-bottom-item">
            <div class="leave-time-header">
              <div>
                <div class="leave-time-header-title">Phân tích loại nghỉ</div>
                <div class="leave-time-header-content-2">Công ty cổ phần công nghệ viễn thông Elcom</div>
                <div class="leave-time-header-content-2">(01/11/2021 - 05/12/2021)</div>
              </div>
            </div>
            <PieChart />
          </div>
          <div class="db-right-bottom-item">
            <div>
              <div class="leave-time-header-title">Danh sách đi muộn về sớm</div>
              <div class="leave-time-header-content-2">Công ty cổ phần công nghệ viễn thông Elcom</div>
              <div class="leave-time-header-content-2">(01/11/2021 - 05/12/2021)</div>
            </div>
            <el-table
              class="table1"
              :data="tableData1"
              style="width: 100%"
            >
              <el-table-column label="" width="70px">
                <template slot-scope="{row}">
                  <img :src="row.imageUrl">
                </template>
              </el-table-column>
              <el-table-column label="" width="220px">
                <template slot-scope="{row}">
                  <div class="table-name">{{ row.name }}</div>
                  <div class="table-department">{{ row.department }}</div>
                </template>
              </el-table-column>
              <el-table-column label="">
                <template slot-scope="{row}">
                  <span class="table-number">{{ row.number }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="db-right-bottom-item">
            <div>
              <div class="leave-time-header-title">Tần suất đi muộn về sớm</div>
              <div class="leave-time-header-content-2">Tất cả các đơn vị</div>
              <div class="leave-time-header-content-2">(01/11/2021 - 05/12/2021)</div>
            </div>
            <el-table
              class="table2"
              :data="tableData2"
              style="width: 100%"
            >
              <el-table-column label="" width="250px">
                <template slot-scope="{row}">
                  <span class="table-name">{{ row.title }}</span>
                </template>
              </el-table-column>
              <el-table-column label="">
                <template slot-scope="{row}">
                  <span class="table-number-2">{{ row.number }} lần</span>
                  <i class="el-icon-caret-right" />
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script>

import LineChart from './chart/lineChart.vue'
import ColumnChart from './chart/columnChart.vue'
import PieChart from './chart/pieChart.vue'

export default {
  components: {
    LineChart,
    ColumnChart,
    PieChart
  },
  data() {
    return {
      departmentId: null,
      departmentList: [],
      time1: 'this_week',
      time2: 'today',
      time3: 'next_week',
      optionTimes: [
        { value: 'today', label: 'Hôm nay' },
        { value: 'this_week', label: 'Tuàn này' },
        { value: 'next_week', label: 'tuần sau' }
      ],
      tableData1: [
        { imageUrl: 'http://103.21.151.166:8683/v1.0/upload/user/avatar/05032024/b194f8d9-ad53-4f87-a93a-82c5bc61b12a.png', name: 'Phạm Minh Thắng', department: 'Phòng ITS', number: 40 }
      ],
      tableData2: [
        { title: 'Trên 6 lần', number: 2 },
        { title: '6 lần', number: 1 },
        { title: '5 lần', number: 3 }
      ]
    }
  },
  created() {
    this.init()
  },
  beforeDestroy() {
    if (this.intervalId) {
      clearInterval(this.intervalId)
    }
  },
  methods: {
  }
}
</script>

<style lang="scss" scoped>
.dashboard-title {
  font-size: 18px;
  font-weight: 600;
}
.dashboard-container {
  background: rgb(235, 233, 233);
}
.dashboard-container {
  padding: 10px 15px;
}
.db-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.db-header-filter {
  height: 60px;
  display: flex;
  gap: 10px;
}
.filter-config {
  font-weight: 600;
  border-radius: 3px;
  height: 35px;
  display: flex;
  gap: 5px;
  align-items: center;
  padding: 0 15px;
  background: white;
  i {
    color: rgb(82, 80, 80);
  }
}
.db-body {
  display: flex;
  gap: 15px
}
.db-left {}
.db-left-item {
  padding: 15px;
  width: 250px;
  margin-bottom: 15px;
  height: 250px;
  background-color: white;

  .db-left-item-title {
    font-weight: 600;
  }

  .db-left-item-number {
    width: 100%;
    height: 150px;
    display: flex;
    div {
      font-size: 44px;
      font-weight: 600;
      margin: auto;
    }
  }
  .db-left-item-detail {
    display: flex;
    justify-content: end;
    color: rgb(241, 107, 107);
    font-size: 16px;
  }

  ::v-deep .el-select {
    input {
      padding: 0;
      width: 90px;
      border: none;
    }
  }
}
.db-right {
  width: 100%;
}
.db-right-top {
  display: flex;
  gap: 15px;
}
.db-right-top-item {
  background: white;
  flex: 1;
  margin-bottom: 15px ;
  height: 400px;
}
.db-right-bottom {
  display: flex;
  gap: 15px;
}
.db-right-bottom-item {
  background: white;
  flex: 1;
  height: 365px;
}

.leave-time-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.leave-time-header-title{
  font-size: 16px;
  font-weight: 600;
}
.leave-time-header-content{
  font-size: 14px;
  color: rgb(241, 107, 107);
  margin-top: 5px;
}
.leave-time-header-content-2{
  font-size: 14px;
  color: rgb(95, 84, 84);
  margin-top: 5px;
}
.leave-time-icon {
  display: flex;
  align-items: center;
  gap: 10px;
  i {
    font-size: 20px;
    font-weight: 600;
    color: rgb(100, 102, 102);
  }

}
.db-right-top-item {
  padding: 15px;
}

.db-right-bottom-item {
  padding: 15px;
}
.table1 {
  img {
    width: 50px;
    border-radius: 50%;
  }
}
.table-department{
  font-size: 14px;
  color: #797676;
}
.table-name{
  font-size: 16px;
  color: black;
}
.table-number {
  font-size: 18px;
  font-weight: 600;
}
.table-number-2 {
  font-size: 16px;
}
</style>
